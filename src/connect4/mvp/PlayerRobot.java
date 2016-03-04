/*
 * Copyright (C) 2016 fabio.epifani
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package connect4.mvp;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * 
 * @author fabio.epifani
 */
public class PlayerRobot implements Player {
  public enum Difficulty {
    Easy,
    Medium,
    Hard
  }
  
	/**
	 * The score given to a state that leads to a win.
	 */
	static final float WIN_REVENUE = 1f;

	/**
	 * The score given to a state that leads to a loss.
	 */
	static final float LOSE_REVENUE = -1f;

	/**
	 * The score given to a state that lead neither to a win nor a loss.
	 */
	static final float UNCERTAIN_REVENUE = 0f;

  private final String name;
  private final int maxDepth;
  private final Random random;
  
  public PlayerRobot() {
    this("Hal", Difficulty.Medium);
  }
  
  public PlayerRobot(String name, Difficulty difficulty) {
    this.name = name;
    this.random = new Random();
    switch (difficulty) {
      case Easy:
        this.maxDepth = 4;
        break;
      case Medium:
        this.maxDepth = 6;
        break;
      default:
        // going deeper than 8 might take a ridiculous time
        this.maxDepth = 8;
        break;
    }
  }
  
  @Override
  public String getName() {
    return this.name;
  }
  
  /**
   * Gets the best move.
   * @param match MatchModel
   * @return 0-based column index
   */
	public int getBestMove(MatchModel match) {
		double maxValue = 2. * Integer.MIN_VALUE;
    ArrayList<Integer> bestMoves = new ArrayList();

		// Search all columns for the one that has the best value.
		// The best score possible is WIN_REVENUE.
		// So if we find a move that has this score, the search can be stopped.
		for (int col = 0; col < match.board.columns; col++) {
      try {
        match.makeMove(col);
        
        double value = this.alphabeta(match, this.maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
        
        match.undoMove();
        
        if (value > maxValue) {
          bestMoves.clear();
          bestMoves.add(col);
 					maxValue = value;
					if (value == WIN_REVENUE) {
						break;
					}         
        }
        
        else if (value == maxValue) {
          bestMoves.add(col);
        }
      }
      catch (Exception ex) {
        // Column is full. Ignore this move.
      }
		}
    
    // moves contains a list of the best moves. A random one will be taken
    return bestMoves.get(this.random.nextInt(bestMoves.size()));
	}

	double alphabeta(MatchModel match, int depth, double alpha, double beta, boolean maximize) {
		boolean hasWinner = match.getStatus() == MatchModel.Status.Winner;
    
		// All these conditions lead to a termination of the recursion
		if (depth == 0 || hasWinner) {
			double score = 0;
      
			if (hasWinner) {
        // If maximize is false, then the robot won
				score = maximize ? LOSE_REVENUE : WIN_REVENUE;
			}
      else {
				score = UNCERTAIN_REVENUE;
			}

			return score / (this.maxDepth - depth + 1);
		}

  	for (int col = 0; col < match.board.columns; col++) {
      try {
        match.makeMove(col);
        
        double alphabeta = this.alphabeta(match, depth - 1, alpha, beta, !maximize);
        
      	if (maximize) {
          // Maximize the revenues
          alpha = Math.max(alpha, alphabeta);
        }
        
        else {
          // Minimize the loss
					beta = Math.min(beta, alphabeta);
        }

        match.undoMove();
        
        if (beta <= alpha) {
          break;
        }
			}
      catch (MatchModel.FullColumnException ex) {
        // Column is full.
      }
		}

    return maximize ? alpha : beta;
	}
}