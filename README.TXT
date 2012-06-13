Rogue
=====

An AP Comp Sci Project to build smart characters to play the ASCII computer game Rogue. In 
this project, I built a distribution of rogue which plays against a monster distribution
created by Steven Weiner.

Rules
-----
The goal of the game for the Rogue is to find a way to stay alive in any specific dungeon.
For many dungeons this may not be possible, but for ones in which there are loops, the
Rogue can last indefinitely. Conversely the monster is attempting to catch the Rogue, and
is thereby trying to make the best possible move towards the Rogue to isolate it, or 
prevent it from winning.

Instructions
------------
In order to start the game, run the static method main within Game, and enter the name of the 
dungeon you would like to play. Dungeons are available within the dungeons folde.

A Couple of Quick Notes
-----------------------
This Rogue isn't perfect yet, and it has a fair amount of work to go. It 
currently assumes it's opponent will always take the best move, shuts down 
when it thinks it can't win, and often times makes the wrong move.

Monster and SiteGrapherSearcher aren't mine, there were written by Steven Weiner for
use in his Monster submission, but they're included so that the game has a smarter
opponent against which to play.