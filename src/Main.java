public class Main {

    public static void main(String[] args) {
	// write your code here
        Game game = new Game();
        game.move('O', 36);
        game.move('O', 35);
        game.move('O', 34);
        game.move('X', 33);
        game.move('X', 41);
        game.move('X', 57);

        System.out.println(game);
        System.out.printf("\nValue: %d\nWinner: %c\n", Game.evaluateBoard(game.board,'O'), game.checkWinner());
    }
}
