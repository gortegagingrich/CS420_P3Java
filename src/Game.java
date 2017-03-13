/**
 * Created by Gabriel on 2017/03/12.
 */
public class Game {
   char[] board;

   public Game() {
      board = new char[64];

      resetBoard();
   }

   public void resetBoard() {
      for (int i = 0; i < board.length; i++) {
         board[i] = i == 36 ? '+' : '-';
      }
   }

   public char checkWinner() {
      int val = evaluateBoard(board, 'O');
      char winner;

      switch (val) {
         case Integer.MAX_VALUE:
            winner = 'O';
            break;
         case Integer.MIN_VALUE:
            winner = 'X';
            break;
         default:
            winner = '-';

      }

      return winner;
   }

   public boolean move(char player, int index) {
      boolean out = false;

      // if chosen move is valid
      if (board[index] == '-' || board[index] == '+') {
         // set square to player's character
         board[index] = player;
         out = true;

         // set adjacent empty squares to '+'
         if (index % 8 > 0 && board[index-1] == '-') {
            board[index-1] = '+';
         }

         if (index % 8 < 7 && board[index+1] == '-') {
            board[index+1] = '+';
         }

         if (index / 8 > 0 && board[index-8] == '-') {
            board[index-8] = '+';
         }

         if (index / 8 < 7 && board[index+8] == '-') {
            board[index+8] = '+';
         }
      }
      return out;
   }

   public static int evaluateBoard(char[] board, char player) {
      int out = 0;
      int i,j,index,streak = 0;
      char prev;

      for (i = 0; i < 8; i++) {
         out += streak;
         streak = 0;
         prev = '-';

         // horizontal
         for (j = 0; j < 8; j++) {
            index = i * 8 + j;

            if (board[index] != '-' && board[index] != '+') {
               if (prev != board[index]) {
                  streak = 1;
                  prev = board[index];
               } else {
                  if (prev == player) {
                     out += streak;
                  } else {
                     out -= streak;
                  }

                  streak += 1;

                  if (streak == 4) {
                     return (prev == player) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
                  }
               }
            } else {
               streak = 0;
            }
         }
      }

      // vertical
      for (j = 0; j < 8; j++) {
         streak = 0;
         prev = '-';

         for (i = 0; i < 8; i++) {
            index = i * 8 + j;

            if (board[index] != '-' && board[index] != '+') {
               if (prev != board[index]) {
                  streak = 1;
                  prev = board[index];
               } else {
                  if (prev == player) {
                     out += streak;
                  } else {
                     out -= streak;
                  }

                  streak += 1;

                  if (streak == 4) {
                     return (prev == player) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
                  }
               }
            } else {
               streak = 0;
            }
         }
      }

      return out;
   }

   public String toString() {
      String out = "";
      char c = 'A';

      for (int i = 1; i < 9; i++) {
         out += String.format("\t%d", i);
      }

      for (int i = 0; i < 64; i++) {
         if (i%8 == 0) {
            out += String.format("\n%c\t", c++);
         }

         out += String.format("%c\t", board[i] == '+' ? '-' : board[i]);
      }

      return out;
   }
}
