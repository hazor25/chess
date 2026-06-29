package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor teamColor;
    private final ChessPiece.PieceType pieceType;


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor = pieceColor;
        this.pieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * @return True if prospective move in bounds
     */
    private boolean inBounds(int row, int col) {
        return row >= 1 && row <= 8
                && col >= 1 && col <= 8;
    }

    /**
     * Shared move adding for King and Knight
     */
    private void addJumpMoves(int[][] PMoves, Collection<ChessMove> moves, ChessBoard board, ChessPosition myPosition) {
        for (int[] move: PMoves) {
            int row = myPosition.getRow()+move[0];
            int col = myPosition.getColumn()+move[1];

            if (inBounds(row, col)) {
                ChessPosition desiredPos = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(desiredPos);

                if (target == null || target.getTeamColor() != teamColor) {
                    moves.add(new ChessMove(myPosition, desiredPos, null));
                }
            }
        }
    }

    /**
     * Shared move adding for Rook, Bishop and Queen
     */
    private void addLoopMoves(int[][] directions, Collection<ChessMove> moves, ChessBoard board, ChessPosition myPosition) {
        for (int[] move: directions) {
            int row = myPosition.getRow()+move[0];
            int col = myPosition.getColumn()+move[1];

            while (inBounds(row, col)) {
                ChessPosition testPos = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(testPos);

                if (target == null) {
                    moves.add(new ChessMove(myPosition, testPos, null));
                    row += move[0];
                    col += move[1];
                }
                else {
                    if (target.getTeamColor() != teamColor) {
                        moves.add(new ChessMove(myPosition, testPos, null));
                    }
                    break;
                }
            }
        }
    }

    /**
     * Adds all 4 possible moves when pawn reaches opposite side
     */
    private void addPromotions(Collection<ChessMove> moves, ChessPosition start, ChessPosition end) {
        moves.add(new ChessMove(start,end,PieceType.QUEEN));
        moves.add(new ChessMove(start,end,PieceType.ROOK));
        moves.add(new ChessMove(start,end,PieceType.BISHOP));
        moves.add(new ChessMove(start,end,PieceType.KNIGHT));
    }


    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        switch (pieceType){
            case PAWN:
                int direction;
                int startRow;
                int promotRow;

                if (teamColor == ChessGame.TeamColor.BLACK) {
                    direction = -1;
                    startRow = 7;
                    promotRow = 1;
                } else {
                    direction = 1;
                    startRow = 2;
                    promotRow = 8;
                }

                int row = myPosition.getRow() + direction;
                int col = myPosition.getColumn();

                if (inBounds(row, col)) {                                 //move forwards handling
                    ChessPosition testPos = new ChessPosition(row, col);

                    if (board.getPiece(testPos) == null) {                  //can move forward

                        if (testPos.getRow() == promotRow) {                    // forward is promotion
                            addPromotions(moves, myPosition, testPos);

                        } else if (myPosition.getRow() == startRow) {           //on starting row
                            moves.add(new ChessMove(myPosition, testPos, null));

                            ChessPosition secondForward = new ChessPosition(testPos.getRow()+direction, myPosition.getColumn());
                            if (board.getPiece(secondForward) == null) {            //second forward position is empty
                                moves.add(new ChessMove(myPosition, secondForward, null));
                            }
                        } else {                        //not on starting or before promotion, but move forward is valid
                            moves.add(new ChessMove(myPosition, testPos, null));
                        }
                    }
                }

                int[] diagMoves = {1, -1};                  //handles diagonal pawn movement
                for (int diag: diagMoves){
                    col = myPosition.getColumn()+diag;
                    if (inBounds(row, col)) {
                        ChessPosition diagonal = new ChessPosition(row, col);
                        ChessPiece diagPiece = board.getPiece(diagonal);
                        if (diagPiece != null) {
                            if (diagPiece.getTeamColor() != teamColor) {
                                if (diagonal.getRow() == promotRow) {
                                    addPromotions(moves, myPosition, diagonal);
                                } else {
                                    moves.add(new ChessMove(myPosition, diagonal, null));
                                }
                            }
                        }
                    }
                }
                break;

            case ROOK:
                int[][] rookMoves = {
                        {1, 0}, {-1, 0},
                        {0, -1}, {0, 1} };

                addLoopMoves(rookMoves, moves, board, myPosition);
                break;

            case BISHOP:
                int[][] bishopMoves = {
                        {1, 1}, {1, -1},
                        {-1, -1}, {-1, 1} };

                addLoopMoves(bishopMoves, moves, board, myPosition);
                break;

            case KNIGHT:
                int[][] knightMoves = {
                        {2, 1}, {2, -1}, {-2, 1},
                        {-2, -1}, {1, 2}, {1, -2},
                        {-1, 2}, {-1, -2} };

                addJumpMoves(knightMoves, moves, board, myPosition);
                break;

            case KING:
                int[][] kingMoves = {
                        {1, -1}, {1, 0}, {1, 1},
                        {0, -1}, {0, 1},
                        {-1, -1}, {-1, 0}, {-1, 1} };

                addJumpMoves(kingMoves, moves, board, myPosition);
                break;

            case QUEEN:
                int [][] queenMoves = {
                        {1, -1}, {1, 0}, {1, 1},
                        {0, -1}, {0, 1},
                        {-1, -1}, {-1, 0}, {-1, 1} };

                addLoopMoves(queenMoves, moves, board, myPosition);
                break;
        }
        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, pieceType);
    }
}
