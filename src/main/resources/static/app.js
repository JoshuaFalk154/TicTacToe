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
  });
}

function disconnect() {
  if (stompClient !== null) {
    stompClient.disconnect();
  }
  setConnected(false);
  console.log("Disconnected");
}

// function showYourPlayerType() {
//   var parentDiv = document.getElementById("game");
//   var yourPlayerType = document.createElement("p");
//   parentDiv.
// }

function updateGame(gameStatus) {
  var parentDiv = document.getElementById("game");
  var childTable = document.getElementById("tictactoetableid");

  if (gameStatus.gameSessionState == "WAITING_ON_PLAYERS") {
    // showElement("waitingOnGame");
    // hideElement("gameOverText");
    // hideElement("yourTurn");
    // hideElement("othersTurn");
    // hideElement("youWon");
    // hideElement("youLost");
    // hideElement("aTie");
    showGameElementsGameWaitingHideElse();
    if (childTable !== null) {
      parentDiv?.removeChild(childTable);
    }
  }

  if (gameStatus.gameSessionState == "RUNNING") {
    if (childTable !== null) {
      parentDiv?.removeChild(childTable);
    }
    childTable = createTable(
      transformArray(gameStatus.board),
      "tictactoetableid",
      "board"
    );
    showGameElementsGameRunningHideElse(gameStatus);
    parentDiv?.appendChild(childTable);
    initEventListenerTable();

    // hideElement("waitingOnGame");
    // hideElement("gameOverText");
    // hideElement("youWon");
    // hideElement("youLost");
    // hideElement("aTie");
    // showIfTrueElseHide("yourTurn", sessionId == gameStatus.playerTurn.id);
    // showIfTrueElseHide("othersTurn", sessionId != gameStatus.playerTurn.id);
    //updatePlayerShowing(gameStatus);
    // showGameElementsGameRunningHideElse();
  }

  if (gameStatus.gameSessionState == "GAME_OVER") {
    // showElement("gameOverText");
    // hideElement("waitingOnGame");
    // hideElement("yourTurn");
    // hideElement("othersTurn");
    // showElement("restartGame");
    showGameElementsGameOverHideElse();

    if (gameStatus.board !== null) {
      if (childTable !== null) {
        parentDiv?.removeChild(childTable);
      }
      childTable = createTable(
        transformArray(gameStatus.board),
        "tictactoetableid",
        "board"
      );
      parentDiv?.appendChild(childTable);
      initEventListenerTable();
    }
    if (gameStatus.outcome == "TIE") {
      showElement("aTie");
    } else if (gameStatus.winner.id == sessionId) {
      showElement("youWon");
    } else {
      showElement("youLost");
    }
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
  if (sessionId == gameStatus.playerTurn.id) {
    var text = "Your Turn: " + gameStatus.playerTurn.assignment;
    //$("#yourTurn").html(text);
    document.getElementById("yourTurn").innerHTML = text;
    showElement("yourTurn");
  } else {
    showElement("othersTurn");
  }

  // showIfTrueElseHide("yourTurn", sessionId == gameStatus.playerTurn.id);
  // showIfTrueElseHide("othersTurn", sessionId != gameStatus.playerTurn.id);
  //updatePlayerShowing(gameStatus);
}

function hideAllChildrenWithId(parentId) {
  const parent = document.getElementById(parentId);
  for (const child of parent.children) {
    if (child.id !== "") {
      hideElement(child.id);
    }
  }
}

function addYourTurnAssignment(gameStatus) {
  if (sessionId == gameStatus.playerTurn.id) {
    var yourTurn = document.getElementById("yourTurn");
    var textNode = document.createTextNode(
      "Your turn " + gameStatus.playerTurn.assignment
    );
  }
}

function updatePlayerShowing(gameStatus) {
  var assignment = gameStatus.playerTurn.assignment;
  var youAre = document.getElementById("youAre");
  var textNode = document.createTextNode("Your are " + assignment);
  youAre.innerHTML = "";
  youAre?.appendChild(textNode);
  showElement("youAre");
}

function getMyPlayer(playerOne, playerTwo) {
  return getPlayerById(playerOne, playerTwo, sessionId);
}

function getPlayerById(playerOne, playerTwo, playerId) {
  if (playerOne.id == playerId) {
    return playerOne;
  } else if (playerTwo == playerId) {
    return playerTwo;
  }
  return null;
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
}

function leaveGame() {
  stompClient.send("/app/leave-game/" + gameSessionId, {});
  gameSubscription.unsubscribe();

  hideAllChildrenWithId("game");

  // hideElement("gameOverText");
  // hideElement("waitingOnGame");
  // hideElement("yourTurn");
  // hideElement("othersTurn");

  var parentDiv = document.getElementById("game");
  var childTable = document.getElementById("tictactoetableid");
  if (childTable !== null) {
    parentDiv?.removeChild(childTable);
  }

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
