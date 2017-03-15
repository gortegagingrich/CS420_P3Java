import java.util.Arrays;

/**
 * Created by Gabriel on 2017/03/13.
 */
public class AI {

   public boolean bestMove(Game game, char player, int time) {
      long start;

      start = System.currentTimeMillis();
      AIRunner runner = new AIRunner(game,player);
      Thread t = new Thread(runner);

      t.start();

      while (System.currentTimeMillis() - start < time * 1000) {
         // wait
      }

      t.interrupt();

      return game.move(player, AIRunner.move);
   }

   public int alphaBeta(Game game, char player, int depth) {
      int maxValue;
      char[] board = Arrays.copyOf(game.getBoardInsecure(),64);

      Node current = new Node(board, player,depth);

      maxValue = findMaximum(current, player, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);

      for (int i = 0; i < 64; i++) {
         if (current.nextStates[i] != null) {
            if (current.nextStates[i].nextValue == maxValue) {
               return i;
            }
         }
      }

      return 0;
   }

   private int findMaximum(Node current, char player, int depth, int alpha, int beta) {
      Node[] nextMoves = current.nextStates;

      char[] board;
      int value = Integer.MIN_VALUE;
      int tempValue;

      // terminal test
      if (depth == 1 || current.getCurrentValue() == Integer.MAX_VALUE || current.getCurrentValue() == Integer.MIN_VALUE) {
         for (int i = 0; i < 64; i++) {
            if (current.state[i] == '+') {
               current.state[i] = player;
               nextMoves[i] = new Node(current.state,player,depth-1);

               if (nextMoves[i].currentValue > value) {
                  value = nextMoves[i].currentValue;
               }
               current.state[i] = '+';
            }
         }

         current.nextValue = value;
         return value;
      }

      // otherwise
      for (int i = 0; i < 64; i++) {
         if (current.state[i] == '+') {
            current.state[i] = player;

            nextMoves[i] = new Node(current.state,player,depth-1);

            // add plusses to next move's state
            if (i % 8 > 0 && nextMoves[i].state[i-1] == '-') {
               nextMoves[i].state[i-1] = '+';
            }

            if (i % 8 < 7 && nextMoves[i].state[i+1] == '-') {
               nextMoves[i].state[i+1] = '+';
            }

            if (i / 8 > 0 && nextMoves[i].state[i-8] == '-') {
               nextMoves[i].state[i-8] = '+';
            }

            if (i / 8 < 7 && nextMoves[i].state[i+8] == '-') {
               nextMoves[i].state[i+8] = '+';
            }

            tempValue = findMinimum(nextMoves[i], player, depth-1, alpha, beta);
            nextMoves[i].nextValue = tempValue;
            value = (value > tempValue) ? value: tempValue;
            alpha = (alpha > value) ? alpha : value;

            if (value > beta) {
               //return value;
            }

            current.state[i] = '+';
         }
      }

      current.nextValue = value;
      return value;
   }

   private int findMinimum(Node current, char player, int depth, int alpha, int beta) {
      Node[] nextMoves = current.nextStates;

      char[] board;
      int value = Integer.MAX_VALUE;
      int tempValue;

      // terminal test
      if (depth == 1 || current.getCurrentValue() == Integer.MAX_VALUE || current.getCurrentValue() == Integer.MIN_VALUE) {
         for (int i = 0; i < 64; i++) {
            if (current.state[i] == '+') {
               current.state[i] = player;
               nextMoves[i] = new Node(current.state,player,depth-1);

               if (nextMoves[i].currentValue < value) {
                  value = nextMoves[i].currentValue;
               }
               current.state[i] = '+';
            }
         }

         current.nextValue = value;
         return value;
      }

      // otherwise
      for (int i = 0; i < 64; i++) {
         if (current.state[i] == '+') {
            current.state[i] = player == 'O' ? 'X' : 'O';

            nextMoves[i] = new Node(current.state,player,depth-1);

            // add plusses to next move's state
            if (i % 8 > 0 && nextMoves[i].state[i-1] == '-') {
               nextMoves[i].state[i-1] = '+';
            }

            if (i % 8 < 7 && nextMoves[i].state[i+1] == '-') {
               nextMoves[i].state[i+1] = '+';
            }

            if (i / 8 > 0 && nextMoves[i].state[i-8] == '-') {
               nextMoves[i].state[i-8] = '+';
            }

            if (i / 8 < 7 && nextMoves[i].state[i+8] == '-') {
               nextMoves[i].state[i+8] = '+';
            }

            tempValue = findMaximum(nextMoves[i], player, depth-1, alpha, beta);
            nextMoves[i].nextValue = tempValue;
            value = (value < tempValue) ? value: tempValue;
            beta = (beta < value) ? beta : value;

            // allows the garbage collector to do its magic and let it get past a depth of 5
            nextMoves[i] = null;

            if (value < alpha) {
               //return value;
            }

            current.state[i] = '+';
         }
      }

      current.nextValue = value;
      return value;
   }

   private class Node {
      char[] state;
      Node[] nextStates;
      int currentValue;
      int nextValue;
      int depth;

      Node(char[] board, char player, int depth) {
         char[] temp;

         currentValue = AI.evaluateBoard(board,player);
         nextValue = Integer.MAX_VALUE - 1; // default value
         state = Arrays.copyOf(board,64);
         nextStates = new Node[64];

         if (depth < 1) {
            nextValue = currentValue;
         }

      }

      void setNextValue(int val) {
         nextValue = val;
      }

      int getNextValue() {
         return nextValue;
      }

      int getCurrentValue() {
         return currentValue;
      }

      int getDepth() {
         return depth;
      }

      char[] getState() {
         return state;
      }
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
}

class AIRunner implements Runnable {
   volatile static int move = 5;

   private Game game;
   private char player;

   AIRunner(Game game, char player) {
      this.game = game;
      this.player = player;
      move = 0;
   }

   @Override
   public void run() {
      // really only handles up through 6 deep
      // tends to run out of memory on my computer otherwise
      AI ai = new AI();

      for (int i = 1; i < 7; i++) {
         move = ai.alphaBeta(game,player,i);
      }
   }
}