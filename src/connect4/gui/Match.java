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
package connect4.gui;

import connect4.mvp.Point;
import connect4.mvp.MatchModel;
import connect4.mvp.MatchPresenter;
import connect4.mvp.MatchView;
import javax.swing.GroupLayout;

public class Match extends javax.swing.JFrame implements MatchView {

  protected static final int BUTTON_WIDTH = 68;
  protected static final int BUTTON_HEIGHT = 48;

  protected MatchPresenter presenter;

  protected javax.swing.Icon iPiece0Small;
  protected javax.swing.Icon iPiece1Small;
  protected javax.swing.Icon iPiece2Small;
  protected javax.swing.Icon iPiece1WinnerSmall;
  protected javax.swing.Icon iPiece2WinnerSmall;
  protected javax.swing.Icon iPieceDis;
  protected javax.swing.Icon iPiece0;
  protected javax.swing.Icon iPiece1;
  protected javax.swing.Icon iPiece2;
  protected javax.swing.Icon iPiece1Winner;
  protected javax.swing.Icon iPiece2Winner;

  protected javax.swing.JLabel[][] board;
  protected javax.swing.JButton[] commands;

  protected javax.swing.JPanel matchInfoPanel;
  protected javax.swing.JLabel lblPlayer1;
  protected javax.swing.JLabel lblPlayer1Name;
  protected javax.swing.JLabel lblPlayer2;
  protected javax.swing.JLabel lblPlayer2Name;

  protected javax.swing.JMenuBar menuBar;

  protected javax.swing.JMenu menuHelp;
  protected javax.swing.JMenuItem menuItemHelp;

  protected javax.swing.JMenu menuMatch;
  protected javax.swing.JMenuItem menuItemNew;
  protected javax.swing.JMenuItem menuItemExit;

  protected javax.swing.JPanel gridPanel;
  protected javax.swing.JPanel cmdPanel;

  public Match() {
    super.setTitle("Connect 4");

    this.presenter = new MatchPresenter(this);
    this.presenter.init(true);
  }

  public void open() {
    this.setVisible(true);
  }

  public void close() {
    this.setVisible(false);
    this.dispose();
  }

  @Override
  public void makeMove(int player, Point point) {
    javax.swing.Icon playerIco;

    playerIco = player == 1 ? this.iPiece1 : this.iPiece2;
    this.board[point.row][point.column].setIcon(playerIco);
  }

  @Override
  public void newMatch(MatchModel model) {
    this.initComponents(model);
    
    this.lblPlayer1Name.setText(model.player1);
    this.lblPlayer2Name.setText(model.player2);
    
    if (model.currentPlayer == 1) {
      this.lblPlayer1Name.setIcon(iPiece1Small);
      this.lblPlayer2Name.setIcon(iPiece0Small);
    }
    else {
      this.lblPlayer1Name.setIcon(iPiece0Small);
      this.lblPlayer2Name.setIcon(iPiece2Small);
    }

    for (int column = 0; column < model.board.columns; column++) {
      this.commands[column].setEnabled(true);

      for (int row = 0; row < model.board.rows; row++) {
        this.board[row][column].setIcon(iPiece0);
      }
    }
  }

  @Override
  public void disableColumn(int x) {
    this.commands[x].setEnabled(false);
  }

  @Override
  public void setCurrentPlayer(int player) {
    if (player == 1) {
      lblPlayer1Name.setIcon(iPiece1Small);
      lblPlayer2Name.setIcon(iPiece0Small);
    }
    else {
      lblPlayer1Name.setIcon(iPiece0Small);
      lblPlayer2Name.setIcon(iPiece2Small);
    }
  }

  @Override
  public void endMatch(int player, Point[] connected) throws IllegalArgumentException {
    javax.swing.Icon winnerIco;

    winnerIco = player == 1 ? iPiece1Winner : iPiece2Winner;

    this.board[connected[0].row][connected[0].column].setIcon(winnerIco);
    this.board[connected[1].row][connected[1].column].setIcon(winnerIco);
    this.board[connected[2].row][connected[2].column].setIcon(winnerIco);
    this.board[connected[3].row][connected[3].column].setIcon(winnerIco);

    if (player == 1) {
      this.lblPlayer1Name.setIcon(iPiece1WinnerSmall);
      this.lblPlayer2Name.setIcon(iPiece0Small);
    }
    else {
      this.lblPlayer1Name.setIcon(iPiece0Small);
      this.lblPlayer2Name.setIcon(iPiece2WinnerSmall);
    }

    this.finish();
  }

  @Override
  public void endMatch() {
    this.lblPlayer1Name.setIcon(iPiece0Small);
    this.lblPlayer2Name.setIcon(iPiece0Small);
    this.finish();
  }

  protected void finish() {
    for (int i = 0; i < this.commands.length; i++) {
      this.commands[i].setEnabled(false);
    }
  }

  protected void commandClick(java.awt.event.ActionEvent evt) {
    int col = Integer.parseInt(evt.getActionCommand());
    this.presenter.makeMove(col);
  }

  protected void actionNew() {
    this.close();
    Match newMatch = new Match();
    newMatch.open();
  }

  protected void actionQuit() {
    this.close();
  }

  protected void initComponents(MatchModel model) {
    int i, j;

    javax.swing.Icon iCmdOn = new javax.swing.ImageIcon(getClass().getResource(GUIConst.SRC_CMDON));
    javax.swing.Icon iCmdOff = new javax.swing.ImageIcon(getClass().getResource(GUIConst.SRC_CMDOFF));
    javax.swing.Icon iCmd = new javax.swing.ImageIcon(getClass().getResource(GUIConst.SRC_CMD));

    board = new javax.swing.JLabel[model.board.rows][model.board.columns];
    commands = new javax.swing.JButton[model.board.columns];

    gridPanel = new javax.swing.JPanel();
    cmdPanel = new javax.swing.JPanel();
    matchInfoPanel = new javax.swing.JPanel();

    lblPlayer1 = new javax.swing.JLabel();
    lblPlayer1Name = new javax.swing.JLabel();
    lblPlayer2 = new javax.swing.JLabel();
    lblPlayer2Name = new javax.swing.JLabel();

    // inizializzo le icone
    iPiece1WinnerSmall = new javax.swing.ImageIcon(getClass().getResource(GUIConst.SRC_PIECE1_WINNER_SMALL));
    iPiece2WinnerSmall = new javax.swing.ImageIcon(getClass().getResource(GUIConst.SRC_PIECE2_WINNER_SMALL));
    iPiece0Small = new javax.swing.ImageIcon(getClass().getResource(GUIConst.SRC_PIECE0_SMALL));
    iPiece1Small = new javax.swing.ImageIcon(getClass().getResource(GUIConst.SRC_PIECE1_SMALL));
    iPiece2Small = new javax.swing.ImageIcon(getClass().getResource(GUIConst.SRC_PIECE2_SMALL));
    iPieceDis = new javax.swing.ImageIcon(getClass().getResource(GUIConst.SRC_PIECE_DIS));
    iPiece0 = new javax.swing.ImageIcon(getClass().getResource(GUIConst.SRC_PIECE0));
    iPiece1 = new javax.swing.ImageIcon(getClass().getResource(GUIConst.SRC_PIECE1));
    iPiece2 = new javax.swing.ImageIcon(getClass().getResource(GUIConst.SRC_PIECE2));
    iPiece1Winner = new javax.swing.ImageIcon(getClass().getResource(GUIConst.SRC_PIECE1_WINNER));
    iPiece2Winner = new javax.swing.ImageIcon(getClass().getResource(GUIConst.SRC_PIECE2_WINNER));

    for (j = 0; j < model.board.columns; j++) {
      commands[j] = new javax.swing.JButton();
      commands[j].setIcon(iCmd);
      commands[j].setDisabledIcon(iCmdOff);
      commands[j].setPressedIcon(iCmdOn);
      commands[j].setEnabled(true);
      commands[j].setBorderPainted(false);
      commands[j].setActionCommand(Integer.toString(j));
      commands[j].addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          commandClick(evt);
        }
      });
      commands[j].setEnabled(false);

      for (i = 0; i < model.board.rows; i++) {
        board[i][j] = new javax.swing.JLabel();
        board[i][j].setIcon(iPieceDis);
      }
    }

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

    setResizable(false);

    gridPanel.setBackground(GUIConst.COLOR_GRID);
    cmdPanel.setBackground(new java.awt.Color(250, 250, 250));
    matchInfoPanel.setBackground(GUIConst.COLOR_BG_MATCH_INFO);

    javax.swing.GroupLayout gridPanelLayout = new javax.swing.GroupLayout(gridPanel);
    javax.swing.GroupLayout cmdPanelLayout = new javax.swing.GroupLayout(cmdPanel);
    javax.swing.GroupLayout matchInfoPanelLayout = new javax.swing.GroupLayout(matchInfoPanel);

    gridPanel.setLayout(gridPanelLayout);
    cmdPanel.setLayout(cmdPanelLayout);
    matchInfoPanel.setLayout(matchInfoPanelLayout);

    lblPlayer1.setText("Player 1:");

    lblPlayer1Name.setIcon(iPiece0Small);
    lblPlayer1Name.setFont(new java.awt.Font("Tahoma", 1, 18));
    lblPlayer1Name.setForeground(new java.awt.Color(255, 0, 0));

    lblPlayer2.setText("Player 2:");

    lblPlayer2Name.setIcon(iPiece0Small);
    lblPlayer2Name.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
    lblPlayer2Name.setForeground(new java.awt.Color(255, 255, 0));

    int matchInfoPanelLabelWidth = Math.round(33 * model.board.columns * 3 / 8);
    int matchInfoPanelNameWidth = 33 * model.board.columns - matchInfoPanelLabelWidth;
    
    matchInfoPanelLayout.setHorizontalGroup(
            matchInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(matchInfoPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(matchInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(matchInfoPanelLayout.createSequentialGroup()
                                    .addComponent(lblPlayer1, javax.swing.GroupLayout.PREFERRED_SIZE, matchInfoPanelLabelWidth, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(lblPlayer1Name, javax.swing.GroupLayout.PREFERRED_SIZE, matchInfoPanelNameWidth, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(lblPlayer2, javax.swing.GroupLayout.PREFERRED_SIZE, matchInfoPanelLabelWidth, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(lblPlayer2Name, javax.swing.GroupLayout.PREFERRED_SIZE, matchInfoPanelNameWidth, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(37, Short.MAX_VALUE))
    );
    matchInfoPanelLayout.setVerticalGroup(
            matchInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(matchInfoPanelLayout.createSequentialGroup()
                    .addGroup(matchInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblPlayer1)
                            .addComponent(lblPlayer1Name, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblPlayer2)
                            .addComponent(lblPlayer2Name, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(20, Short.MAX_VALUE))
    );

    GroupLayout.SequentialGroup cmdGroupSequential = cmdPanelLayout.createSequentialGroup();    
    for (int col = 0; col < model.board.columns; col++) {
      if (col > 0) {
        cmdGroupSequential.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED);
      }
      cmdGroupSequential.addComponent(commands[col], BUTTON_WIDTH, BUTTON_WIDTH, BUTTON_WIDTH);
    }
    
    cmdPanelLayout.setHorizontalGroup(
            cmdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cmdPanelLayout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(cmdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(cmdGroupSequential))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    GroupLayout.ParallelGroup cmdGroupParallel = cmdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE);
    for (int col = 0; col < model.board.columns; col++) {
      cmdGroupParallel.addComponent(commands[col], BUTTON_HEIGHT, BUTTON_HEIGHT, BUTTON_HEIGHT);
    }
    
    cmdPanelLayout.setVerticalGroup(
            cmdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cmdPanelLayout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(cmdGroupParallel)
                    .addContainerGap(48, Short.MAX_VALUE))
    );

    GroupLayout.ParallelGroup boardGroupParallel = gridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING);
        
    for (int row = model.board.rows - 1; row >= 0; row--) {
      GroupLayout.SequentialGroup rowGroup = gridPanelLayout.createSequentialGroup();

      for (int col = 0; col < model.board.columns; col++) {
        if (col > 0) {
          rowGroup.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED);
        }
        rowGroup.addComponent(board[row][col]);
      }
      
      boardGroupParallel.addGroup(rowGroup);
    }
    
    gridPanelLayout.setHorizontalGroup(
      gridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(gridPanelLayout.createSequentialGroup()
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(boardGroupParallel)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    GroupLayout.SequentialGroup boardGroupSequential = gridPanelLayout.createSequentialGroup();
    
    boardGroupSequential.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
    
    for (int row = model.board.rows - 1; row >= 0; row--) {
      GroupLayout.ParallelGroup rowGroup = gridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE);
      
      for (int col = 0; col < model.board.columns; col++) {
        rowGroup.addComponent(board[row][col]);
      }
      
      boardGroupSequential.addGroup(rowGroup);
      boardGroupSequential.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED);
    }
    
    boardGroupSequential.addContainerGap(48, Short.MAX_VALUE);
    
    gridPanelLayout.setVerticalGroup(gridPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(boardGroupSequential));

    menuBar = new javax.swing.JMenuBar();

    menuBar.setMaximumSize(new java.awt.Dimension(60, 21));
    menuBar.setMinimumSize(new java.awt.Dimension(60, 21));

    // MENU
    menuMatch = new javax.swing.JMenu();
    menuMatch.setText("Match");

    menuItemNew = new javax.swing.JMenuItem();
    menuItemExit = new javax.swing.JMenuItem();

    menuItemNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
    menuItemNew.setText("New");
    menuItemNew.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        actionNew();
      }
    });
    menuMatch.add(menuItemNew);

    menuItemExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
    menuItemExit.setText("Quit");
    menuItemExit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        actionQuit();
      }
    });
    menuMatch.add(menuItemExit);

    menuBar.add(menuMatch);

    setJMenuBar(menuBar);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(matchInfoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(cmdPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(gridPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
    );
    layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(matchInfoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, BUTTON_HEIGHT + 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdPanel, javax.swing.GroupLayout.PREFERRED_SIZE, BUTTON_HEIGHT + 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(gridPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    pack();
  }
}
