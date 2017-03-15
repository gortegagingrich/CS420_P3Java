import java.util.Scanner;

public class Main {

   public static void main(String[] args) {
      Game game = new Game();
      AI ai = new AI();
      Scanner scan = new Scanner(System.in);
      String temp;

      System.out.printf("How much time should the AI have? ");
      int time = Integer.parseInt(scan.nextLine());

      System.out.printf("Would you like to go first? (Y/n): ");
      temp = scan.nextLine();
      boolean first = temp.length() < 1 || temp.charAt(0) != 'n';

      for (int i = 0; i < 32; i++) {
         if (first) {
            System.out.printf("%s\nenter a move: ", game);
            game.move('O', scan.nextLine());

            if (game.checkWinner() != '-') {
               break;
            }

            ai.bestMove(game, 'X', time);

         } else {
            ai.bestMove(game, 'O', time);

            if (game.checkWinner() != '-') {
               break;
            }
            System.gc();

            System.out.printf("%s\nenter a move: ", game);
            game.move('X', scan.nextLine());
         }

         if (game.checkWinner() != '-') {
            break;
         }
         System.gc();
      }

      System.out.println(game);
      System.out.printf("\nWinner: %c\n", game.checkWinner());

      game.resetBoard();
   }
}
