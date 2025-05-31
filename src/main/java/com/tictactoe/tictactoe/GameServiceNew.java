package com.tictactoe.tictactoe;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
//@RequiredArgsConstructor
@NoArgsConstructor
public class GameServiceNew {

    GameSession gameSession = new GameSession("randomStr");

}
