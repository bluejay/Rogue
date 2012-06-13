PROJECT TITLE: Rogue
PURPOSE OF PROJECT: AP Comp Sci Project to Make a Competitive Rogue Distribution
VERSION or DATE: 6/4/12
HOW TO START THIS PROJECT: Game#main
AUTHORS: Jay Palekar, Janet Smith, Steven Weiner (Monster Implementation), et. all
USER INSTRUCTIONS:
  To launch a game, run Game#main(String [] args) and pass in the name of the dungeon you want to play.
  Current dungeons list from a through R.

Note: The rogue isn't perfect yet, it assumes optimal solutions, and so 
      will sometimes not make a move (though in this case it tries to go to the 
      site farthest from monster), or make the wrong move, which can be fixed 
      by altering the heuristic, but so far this is the best resulting heuristic 
      algorithem I've been able to make.

Also: Monster and SiteGraphSearcher are from Steven Weiner's Monster implementation,
      which I was using to test my implementation of Rogue.

ANALYSIS: Barring a couple of extreme dungeons (P and R), I was able to win on 
          pretty much every Rogue winnable dungeon provided. The tradeoff that 
          my implementation most generally dealt with was between limiting 
          the time my approach needed per step, and the accuracy with which
          it picked the best move. On some dungeons such as H, at a depth of
          6 the minimax produces the wrong answer, but upped to 8 which is up to
          100 times slower it makes the right move. I was able to improve this
          a lot with Alpha-Beta pruning. In addition, corridors and rooms have 
          different depths since the they have different numbers of neighbors and
          so their complexity doesn't increase at the same rate, allowing me the 
          benefit of high foresite, with reasonable speed for corridors.