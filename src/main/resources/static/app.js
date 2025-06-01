// @ts-check

var stompClient = null;

function setConnected(connected) {
  $("#connect").prop("disabled", connected);
  $("#disconnect").prop("disabled", !connected);
  if (connected) {
    $("#conversation").show();
  } else {
    $("#conversation").hide();
  }
  $("#greetings").html("");
}

function connect() {
  var socket = new SockJS("/stomp-endpoint");
  stompClient = Stomp.over(socket);
  stompClient.connect({}, function (frame) {
    setConnected(true);
    console.log("Connected: " + frame);
    stompClient.subscribe("/topic/greetings", function (greeting) {
      showGreeting(JSON.parse(greeting.body));
    });

    stompClient.subscribe("/topic/game", function (gameState) {
      console.log("JSON.parse(gameState.body");
      console.log(JSON.parse(gameState.body));
      updateGame(JSON.parse(gameState.body));
    });
  });
}

function disconnect() {
  if (stompClient !== null) {
    stompClient.disconnect();
  }
  setConnected(false);
  console.log("Disconnected");
}

function sendName() {
  stompClient.send(
    "/app/hello",
    {},
    JSON.stringify({ name: $("#name").val() })
  );
}

function showGreeting(message) {
  $("#greetings").append("<tr><td>" + message.message + "</td></tr>");
}

function updateGame(gameState) {
  var parentDiv = document.getElementById("game");
  var childTable = document.getElementById("tictactoetableid");
  if (childTable !== null) {
    parentDiv?.removeChild(childTable);
  }
  childTable = createTable(
    transformArray(gameState.board),
    "tictactoetableid",
    "board"
  );
  parentDiv?.appendChild(childTable);
  initEventListenerTable();
  if (gameState.gameOver) {
    $("#game").prepend(
      "<h2>Game Over</h2>",
      "<h3> The Winner is: " + gameState.winner + "</h3>"
    );
    showElement("restartGame");
  } else {
    hideElement("none");
  }
}

function showElement(elementId) {
  var element = document.getElementById(elementId);
  if (element?.style.display === "none") {
    element.style.display = "block";
  }
}

function hideElement(elementId) {
  var element = document.getElementById(elementId);
  if (element?.style.display !== "none") {
    element.style.display = "block";
  }
}

function transformArray(board) {
  board.forEach((row, i1) => {
    var row = board[i1];
    row.forEach((cell, i2) => {
      if (cell == 0) {
        row[i2] = " ";
      } else if (cell == 1) {
        row[i2] = "X";
      } else if (cell == 2) {
        row[i2] = "O";
      }
    });
  });

  return board;
}

function createTable(tableData, tableId, tableClass) {
  var table = document.createElement("table");
  table.setAttribute("id", tableId);
  table.setAttribute("class", tableClass);
  var tableBody = document.createElement("tbody");

  tableData.forEach((rowData) => {
    var row = document.createElement("tr");

    rowData.forEach((cellData) => {
      var cell = document.createElement("td");
      cell.appendChild(document.createTextNode(cellData));
      row.appendChild(cell);
    });

    tableBody.appendChild(row);
  });

  table.appendChild(tableBody);
  initEventListenerTable();
  return table;
}

function startGame() {
  stompClient.send("/app/join-game", {});
}

function restartGame() {
  $("#game").empty();
  hideElement("restartGame");
  stompClient.send("/app/restart-game", {});
}

function makeMove(row, col) {
  var move = {
    row: row,
    col: col,
  };
  var moveJSON = JSON.stringify(move);
  console.log(moveJSON);
  stompClient.send("/app/take-move", {}, moveJSON);
}

function initEventListenerTable() {
  const cells = document.querySelectorAll("td");
  cells.forEach((cell) => {
    cell.addEventListener("click", () =>
      makeMove(cell.closest("tr").rowIndex, cell.cellIndex)
    );
  });
}

$(function () {
  $("form").on("submit", function (e) {
    e.preventDefault();
  });
  $("#connect").click(function () {
    connect();
  });
  $("#disconnect").click(function () {
    disconnect();
  });
  $("#send").click(function () {
    sendName();
  });
  $("#startGame").click(function () {
    startGame();
  });
  $("#restartGame").click(function () {
    restartGame();
  });

  //$( "#moveButton" ).click(function() { makeMove(); });
});
