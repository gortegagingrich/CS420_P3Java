/**
 * Created by Gabriel on 2017/03/12.
 */
public class Game {
   private char[] board;

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
      int val = AI.evaluateBoard(board, 'O');
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
      if (index >= 0 && index < 64 && (board[index] == '-' || board[index] == '+')) {
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

   public boolean move(char player, String move) {
      int index;

      if (move.length() == 2) {
         index = 8 * (Character.toUpperCase(move.charAt(0)) - 'A');
         index += move.charAt(1) - '1';

         return (move(player, index));
      }

      return false;
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

   char[] getBoardInsecure() {
      return board;
   }
}
