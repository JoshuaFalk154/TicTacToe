// @ts-check

var stompClient = null;
var sessionId = null;
var gameSessionId = null;
var gameSubscription = null;

window.onload = function () {
  connect();
};

function connect() {
  var socket = new SockJS("/stomp-endpoint");
  stompClient = Stomp.over(socket);
  stompClient.connect({}, function (frame) {
    sessionId = /\/([^\/]+)\/websocket/.exec(socket._transport.url)[1];
    console.log("Connected: " + frame);
  });
}

function updateGame(gameStatus) {
  if (gameStatus.gameSessionState == "WAITING_ON_PLAYERS") {
    showGameElementsGameWaitingHideElse();
    removeTableIfExists("game", "tictactoetableid");
  }

  if (gameStatus.gameSessionState == "RUNNING") {
    showGameElementsGameRunningHideElse(gameStatus);
    recreateTTTTable(gameStatus);
  }

  if (gameStatus.gameSessionState == "GAME_OVER") {
    showGameElementsGameOverHideElse();
    recreateTTTTable(gameStatus);

    if (gameStatus.outcome == "TIE") {
      showElement("aTie");
    } else if (gameStatus.winner.id == sessionId) {
      showElement("youWon");
    } else {
      showElement("youLost");
    }
  }
}

function removeTableIfExists(parentId, tableId) {
  var parentDiv = document.getElementById(parentId);
  var childTable = document.getElementById(tableId);
  if (childTable !== null) {
    parentDiv?.removeChild(childTable);
  }
}

function recreateTTTTable(gameStatus) {
  var parentDiv = document.getElementById("game");
  var childTable = document.getElementById("tictactoetableid");
  removeTableIfExists("game", "tictactoetableid");

  if (gameStatus.board !== null) {
    childTable = createTable(
      transformArray(gameStatus.board),
      "tictactoetableid",
      "board"
    );
    parentDiv?.appendChild(childTable);
    initEventListenerTable();
  }
}

function showGameElementsGameWaitingHideElse() {
  hideAllChildrenWithId("game");
  showElement("waitingOnGame");
}

function showGameElementsGameOverHideElse() {
  hideAllChildrenWithId("game");
  showElement("gameOverText");
  showElement("restartGame");
}

function showGameElementsGameRunningHideElse(gameStatus) {
  hideAllChildrenWithId("game");
  hideElement("restartGame");
  if (sessionId == gameStatus.playerTurn.id) {
    var text = "Your Turn: " + gameStatus.playerTurn.assignment;
    document.getElementById("yourTurn").innerHTML = text;
    showElement("yourTurn");
  } else {
    showElement("othersTurn");
  }
}

function hideAllChildrenWithId(parentId) {
  const parent = document.getElementById(parentId);
  for (const child of parent.children) {
    if (child.id !== "") {
      hideElement(child.id);
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

async function joinGame() {
  var url = window.location.origin;
  url += "/join-game?";
  console.log(url);
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
    hideElement("joinGame");
    showElement("leaveGame");
    subToCurrentGameSession();
    getCurrentGame();
  } catch (error) {
    console.error(error.message);
  }
}

function getCurrentGame() {
  stompClient.send("/app/trigger-game-update/" + gameSessionId, {});
}

function subToCurrentGameSession() {
  if (gameSessionId == null) {
    console.log("error, no current session id");
    return;
  }
  gameSubscription = stompClient.subscribe(
    "/topic/current-game/" + gameSessionId,
    function (gameState) {
      updateGame(JSON.parse(gameState.body));
    }
  );
}

function restartGame() {
  stompClient.send("/app/restart-game/" + gameSessionId, {});
  hideElement("restartGame");
}

function leaveGame() {
  stompClient.send("/app/leave-game/" + gameSessionId, {});
  gameSubscription.unsubscribe();
  hideAllChildrenWithId("game");
  var parentDiv = document.getElementById("game");
  var childTable = document.getElementById("tictactoetableid");
  if (childTable !== null) {
    parentDiv?.removeChild(childTable);
  }

   hideElement("leaveGame");
   showElement("joinGame");
   hideElement("restartGame");
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
  $("#restartGame").click(function () {
    restartGame();
  });
  $("#joinGame").click(function () {
    joinGame();
  });
  $("#leaveGame").click(function () {
    leaveGame();
  });
});
