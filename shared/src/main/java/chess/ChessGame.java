package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor turn;
    ChessBoard board = new ChessBoard();

    public ChessGame() {
        board.resetBoard();
        turn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (board.getPiece(startPosition) == null) {
            return null;
        }
        Collection<ChessMove> validMoves = new ArrayList<>();

        ChessPiece testPiece = board.getPiece(startPosition);
        Collection<ChessMove> pieceMoves = testPiece.pieceMoves(board, startPosition);
        ChessBoard original = board;

        for (ChessMove move: pieceMoves) {
            ChessBoard testBoard = new ChessBoard(original);

            testBoard.addPiece(move.getStartPosition(), null);
            testBoard.addPiece(move.getEndPosition(), testPiece);

            board = testBoard;

            if (!isInCheck(testPiece.getTeamColor())) {
                validMoves.add(move);
            }
        }
        board = original;
        return validMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece movingPiece = board.getPiece(move.getStartPosition());
        if (movingPiece == null) {
            throw new InvalidMoveException("No piece at position " + move.getStartPosition());
        }

        TeamColor color = movingPiece.getTeamColor();
        if (color != getTeamTurn()) {
            throw new InvalidMoveException("Invalid move, not this team's turn");
        }

        if (validMoves(move.getStartPosition()).contains(move)) {
            board.addPiece(move.getStartPosition(), null);
            if (move.getPromotionPiece() == null) {
                board.addPiece(move.getEndPosition(), movingPiece);
            } else {
                board.addPiece(move.getEndPosition(), new ChessPiece(movingPiece.getTeamColor(), move.getPromotionPiece()));
            }

        } else {
            throw new InvalidMoveException("Invalid move from " + move.getStartPosition() + " to " + move.getEndPosition());
        }

        setTeamTurn(color == TeamColor.WHITE? TeamColor.BLACK: TeamColor.WHITE);
    }

    private TeamColor oppositeColor(TeamColor color) {
        return color == TeamColor.WHITE? TeamColor.BLACK : TeamColor.WHITE;
    }

    private Collection<ChessPosition> getPieces(TeamColor team) {
        Collection<ChessPosition> pieces = new ArrayList<>();

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                if (piece != null && piece.getTeamColor() == team) {
                    pieces.add(pos);
                }
            }
        }
        return pieces;
    }

    // helper to find the king and debloat inCheck function //
    private ChessPosition getKingPos(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition findKing = new ChessPosition(row, col);
                ChessPiece testPiece = board.getPiece(findKing);
                if (testPiece != null) {
                    if (testPiece.getTeamColor() == teamColor && testPiece.getPieceType() == ChessPiece.PieceType.KING) {
                        return findKing;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingsPos = getKingPos(teamColor);

        for (ChessPosition pos: getPieces(oppositeColor(teamColor))) {
            ChessPiece testPiece = board.getPiece(pos);
            Collection<ChessMove> moves = testPiece.pieceMoves(board, pos);
            for (ChessMove move: moves) {
                if (move.getEndPosition().equals(kingsPos)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }

        for (ChessPosition pos: getPieces(teamColor)) {
            Collection<ChessMove> moves = validMoves(pos);
            if (!moves.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        for (ChessPosition pos: getPieces(teamColor)) {
            Collection<ChessMove> moves = validMoves(pos);
            if (!moves.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, board);
    }
}
