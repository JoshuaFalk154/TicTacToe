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
  childTable = createTable(gameState.board, "tictactoetableid");
  parentDiv?.appendChild(childTable);
  initEventListenerTable();
  if (gameState.gameOver) {
    $("#game").append("<p>Game Over</p>");
  }
}

function createTable(tableData, tableId) {
  var table = document.createElement("table");
  table.setAttribute("id", tableId);
  var tableBody = document.createElement("tbody");

  tableData.forEach(function (rowData) {
    var row = document.createElement("tr");

    rowData.forEach(function (cellData) {
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

function updateTable(tableData, tableId) {
  var table = document.getElementById(tableId);
  var tableBody = table.tBodies[0];

  tableData.forEach(function (rowData) {
    var row = document.createElement("tr");

    rowData.forEach(function (cellData) {
      var cell = document.createElement("td");
      cell.appendChild(document.createTextNode(cellData));
      row.appendChild(cell);
    });

    tableBody.appendChild(row);
  });

  table.appendChild(tableBody);
  document.body.appendChild(table);
  initEventListenerTable();
}

function startGame() {
  stompClient.send("/app/start-game", {});
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
  //$( "#moveButton" ).click(function() { makeMove(); });
});
