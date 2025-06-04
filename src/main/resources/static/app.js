// @ts-check

var stompClient = null;
var sessionId = null;
var gameSessionId = null;
var gameSubscription = null;

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
    sessionId = /\/([^\/]+)\/websocket/.exec(socket._transport.url)[1];
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

  if (gameState.gamePhase == "WAITING_FOR_PLAYERS") {
    showElement("waitingOnGame");
    hideElement("gameOverText");
    hideElement("yourTurn");
    hideElement("othersTurn");
    if (childTable !== null) {
      parentDiv?.removeChild(childTable);
    }
  }

  if (gameState.gamePhase == "IN_PROGRESS") {
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

    hideElement("waitingOnGame");
    hideElement("gameOverText");
    showIfTrueElseHide(
      "yourTurn",
      sessionId == gameState.playersTurnPlayerSessionId
    );
    showIfTrueElseHide(
      "othersTurn",
      sessionId != gameState.playersTurnPlayerSessionId
    );
  }

  if (gameState.gamePhase == "GAME_OVER") {
    showElement("gameOverText");
    hideElement("waitingOnGame");
    hideElement("yourTurn");
    hideElement("othersTurn");
    showElement("restartGame");

    if (gameState.board !== null) {
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
    }
    // TODO remove table
  }
}

function showIfTrueElseHide(elementId, boolean) {
  var element = document.getElementById(elementId);
  // show element
  if (boolean) {
    if (element?.style.display === "none") {
      element.style.display = "block";
      return;
    }
  }
  // hide element
  else {
    if (element?.style.display !== "none") {
      element.style.display = "none";
    }
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
    element.style.display = "none";
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
  // stompClient.send("/app/init-game/" + gameSessionId, {});
}

async function joinGame() {
  const url = "http://localhost:8080/join-game?";
  try {
    const response = await fetch(
      url +
        new URLSearchParams({
          simpSessionId: sessionId,
        })
    );

    if (!response.ok) {
      throw new Error(`Response status: ${response.status}`);
    }

    gameSessionId = await response.text();
    subToCurrentGameSession();
    getCurrentGame();
  } catch (error) {
    console.error(error.message);
  }
}

function getCurrentGame() {
  stompClient.send("/app/trigger-game-update/" + gameSessionId, {});
}

// function initGame() {
//   stompClient.send("/app/init-game/" + gameSessionId, {});
// }

function subToCurrentGameSession() {
  if (gameSessionId == null) {
    console.log("error, no current session id");
    return;
  }
  gameSubscription = stompClient.subscribe(
    "/topic/current-game/" + gameSessionId,
    function (greeting) {
      showGreeting(JSON.parse(greeting.body));
    }
  );
  stompClient.subscribe(
    "/topic/current-game/" + gameSessionId,
    function (gameState) {
      updateGame(JSON.parse(gameState.body));
    }
  );
}

// function restartGame() {
//   $("#game").empty();
//   hideElement("restartGame");
// }

function restartGame() {
  stompClient.send("/app/restart-game/" + gameSessionId, {});
}

function leaveGame() {
  stompClient.send("/app/leave-game/" + gameSessionId, {});
  gameSubscription.unsubscribe();
  hideElement("waitingOnGame");
  hideElement("gameOverText");
  gameSessionId = null;
}

function makeMove(row, col) {
  var move = {
    row: row,
    col: col,
  };
  var moveJSON = JSON.stringify(move);
  console.log(moveJSON);
  stompClient.send("/app/take-move/" + gameSessionId, {}, moveJSON);
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
  $("#restartGame").click(function () {
    restartGame();
  });
  $("#joinGame").click(function () {
    joinGame();
  });
  $("#startGame").click(function () {
    startGame();
  });
  $("#leaveGame").click(function () {
    leaveGame();
  });
});
