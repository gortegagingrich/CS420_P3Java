import java.util.Arrays;

/**
 * Created by Gabriel on 2017/03/13.
 */
public class AI {

   /**
    * Creates thread that runs alpha-beta pruning algorithm with iterative deepening that gets cut off at the given time
    * limit.
    * Then, it plays the most recently generated move.
    *
    * @param game   the game itself
    * @param player the AI's character
    * @param time   how long it should run in seconds
    *
    * @return true or false whether or not the move generated is possible
    */
   public boolean bestMove(Game game, char player, int time) {
      long start;

      start = System.currentTimeMillis();
      AIRunner runner = new AIRunner(game, player);
      Thread t = new Thread(runner);

      t.start();

      while (System.currentTimeMillis() - start < time * 1000) {
         // wait
      }

      t.interrupt();

      return game.move(player, AIRunner.move);
   }

   /**
    * Implementation of alpha-beta pruning.
    *
    * @param game   the game itself to take initial board state from
    * @param player the player's character
    * @param depth  how deep it should go
    *
    * @return the index of the move to make
    */
   public int alphaBeta(Game game, char player, int depth) {
      int maxValue;
      char[] board = Arrays.copyOf(game.getBoardInsecure(), 64);

      // create node for default board state
      Node current = new Node(board, player, depth);

      // start algorithm by calling findMaximum on default state with given depth
      maxValue = findMaximum(current, player, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);

      // then it finds which of the possible moves would have the value returned
      for (int i = 0; i < 64; i++) {
         if (current.nextStates[i] != null) {
            if (current.nextStates[i].nextValue == maxValue) {
               return i;
            }
         }
      }

      // this should not be reached
      return 0;
   }

   /**
    * Returns the value of the player's best possible move
    *
    * @param current Node describing current state
    * @param player  player's character
    * @param depth   current depth
    * @param alpha
    * @param beta
    *
    * @return value of best next move for player
    */
   private int findMaximum(Node current, char player, int depth, int alpha, int beta) {
      Node[] nextMoves = current.nextStates;

      char[] board;
      int value = Integer.MIN_VALUE;
      int tempValue;

      // terminal test
      if (depth == 1 || current.getCurrentValue() == Integer.MAX_VALUE ||
          current.getCurrentValue() == Integer.MIN_VALUE) {
         for (int i = 0; i < 64; i++) {
            if (current.state[i] == '+') {
               current.state[i] = player;
               nextMoves[i] = new Node(current.state, player, depth - 1);

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

            nextMoves[i] = new Node(current.state, player, depth - 1);

            // add plusses to next move's state
            if (i % 8 > 0 && nextMoves[i].state[i - 1] == '-') {
               nextMoves[i].state[i - 1] = '+';
            }

            if (i % 8 < 7 && nextMoves[i].state[i + 1] == '-') {
               nextMoves[i].state[i + 1] = '+';
            }

            if (i / 8 > 0 && nextMoves[i].state[i - 8] == '-') {
               nextMoves[i].state[i - 8] = '+';
            }

            if (i / 8 < 7 && nextMoves[i].state[i + 8] == '-') {
               nextMoves[i].state[i + 8] = '+';
            }

            tempValue = findMinimum(nextMoves[i], player, depth - 1, alpha, beta);
            nextMoves[i].nextValue = tempValue;
            value = (value > tempValue) ? value : tempValue;
            alpha = (alpha > value) ? alpha : value;

            current.state[i] = '+';
         }
      }

      current.nextValue = value;
      return value;
   }

   /**
    * Finds worst move oponent can make (with minimum value in terms of player)
    *
    * @param current node defining current board state
    * @param player  player's character
    * @param depth   current depth
    * @param alpha
    * @param beta
    *
    * @return minimum of values of next possible moves
    */
   private int findMinimum(Node current, char player, int depth, int alpha, int beta) {
      Node[] nextMoves = current.nextStates;

      char[] board;
      int value = Integer.MAX_VALUE;
      int tempValue;

      // terminal test
      if (depth == 1 || current.getCurrentValue() == Integer.MAX_VALUE ||
          current.getCurrentValue() == Integer.MIN_VALUE) {
         for (int i = 0; i < 64; i++) {
            if (current.state[i] == '+') {
               current.state[i] = player;
               nextMoves[i] = new Node(current.state, player, depth - 1);

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

            nextMoves[i] = new Node(current.state, player, depth - 1);

            // add plusses to next move's state
            if (i % 8 > 0 && nextMoves[i].state[i - 1] == '-') {
               nextMoves[i].state[i - 1] = '+';
            }

            if (i % 8 < 7 && nextMoves[i].state[i + 1] == '-') {
               nextMoves[i].state[i + 1] = '+';
            }

            if (i / 8 > 0 && nextMoves[i].state[i - 8] == '-') {
               nextMoves[i].state[i - 8] = '+';
            }

            if (i / 8 < 7 && nextMoves[i].state[i + 8] == '-') {
               nextMoves[i].state[i + 8] = '+';
            }

            tempValue = findMaximum(nextMoves[i], player, depth - 1, alpha, beta);
            nextMoves[i].nextValue = tempValue;
            value = (value < tempValue) ? value : tempValue;
            beta = (beta < value) ? beta : value;

            // allows the garbage collector to do its magic and let it get past a depth of 5
            nextMoves[i] = null;

            current.state[i] = '+';
         }
      }

      current.nextValue = value;
      return value;
   }

   /**
    * Node that describes a board state whose children are possible successive states
    */
   private class Node {
      char[] state;
      Node[] nextStates;
      // value of current state mainly to check if it would terminate the game
      int    currentValue;
      // value of next move
      int    nextValue;
      int    depth;

      /**
       * Initializes currentValue to the value of the current board state and nextValue to a constant
       *
       * @param board  current board state
       * @param player player's character
       * @param depth  current depth (not used)
       */
      Node(char[] board, char player, int depth) {
         char[] temp;

         currentValue = AI.evaluateBoard(board, player);
         nextValue = Integer.MAX_VALUE - 1; // default value
         state = Arrays.copyOf(board, 64);
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

   /**
    * Evaluation function for simplified 8,8,4-game.
    * Works by keeping track of horizontal and vertical streaks and weights them based on length.
    * If a streak is for the player, it is added to the value.  If it is for the opponent, it is subtracted from the
    * value.
    *
    * @param board  current board state
    * @param player player's character
    *
    * @return heuristic value of the board
    */
   public static int evaluateBoard(char[] board, char player) {
      int out = 0;
      int i, j, index, streak = 0;
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

/**
 * Implementation of Runnable interface to be able to run alpha-beta with iterative deepening.
 */
class AIRunner implements Runnable {
   // stores index of generated moves
   volatile static int move = 0;

   private Game game;
   private char player;

   /**
    * Resets AIRunner.move and sets other private instance variables
    *
    * @param game
    * @param player
    */
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

      // increases depth incrementally and stores generated move
      for (int i = 1; i < 7; i++) {
         move = ai.alphaBeta(game, player, i);
      }
   }
}