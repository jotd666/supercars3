                        SUPERCARS 3
                        -----------

A Supercars (Magnetic Fields) remake with many features by JOTD

Features:

- high-resolution graphics (enhancements to original graphics, rendered graphics, 256 color
  screens ripped from Supercars International)
- enhanced sound
- 2 player mode
- joypad and keyboard control
- rendered & multicolored cars
- all 21 Supercars 2 circuits
- all 10 Supercars circuits (with high cpu cars aggressivity for more fun!)
- custom circuits
- car selection
- full range of animated weapons
- powerful circuit editor
- shades/shadows
- moving gates
- trains
- fully localized
- shop & repairs
- 256 color localizable communication screens
- Tries to keep up with the Supercars II "spirit"
- optional in-game music
- optional remixed title music
- works on Windows XP, Windows 7, MacOSX
- has a dedicated Pandora port (by Seb: http://repo.openpandora.org/?page=detail&app=supercars3_ptitseb)

!!!!! Please read below before writing me about a bug. It is very likely to be on either of
the lists below !!!!!

High-priority To Do:

- sc1 course test
- hard course test
- better computer car guidance when collided

Medium/Low-priority To Do:

- if music is set to "in menus", escaping does not reset the title music
- differentiate skid according to sceneries (rock, grass, snow)
- oh and... maybe even a lil weapons counter so you know how many ... whatever you have left
 (like on supercars international)
- ah, yeah- maybe have the background music a lil louder as default... and allow users to set the
  levels in the options :>
- finish supercars 1 locale
- credit the original SC2 people: Communication Section Characters by Jeremy Smith, Additional  Work Peter Liggett, joystick code, additional music
- fix hiscore remixes
- add more new circuits in the "remix" circuit set
- zone test should be better when car is reappearing (if zone too small then can explode at once)
- car behaviour: slope acceleration/deceleration is not right (should add/sub, not multiply)
- gfx: full-screen snapshot (F2) has problems
- editor: add desynchronized gate system (for hard level 2), rewrite region editor using visual editor to do so
- management: Support repair with random damage
- AI: fix the problem when the computer car goes backwards and is stuck against a wall
- second player joystick control

Minor To Do:

- gfx: change font in shop
- auto car selection
- check if sound threads are really killed when exiting the game
- gfx: explosion in a tunnel: not properly clipped
- sound: sometimes races start at once (no horn)
- text: finish french localization of communication screens: insurance, reporter, sponsor, arms dealer
- editor: when creating a route, renumber points to put last point as the last point of finish zone
- gfx: car start under a tunnel problem: completely hidden (race 7 of hard level)
- keys not responding properly in menus
- AI: variable aim for computer cars (not for homers)
- AI: "smart" option for enemy cars (for weapon firing depending on the weapon, train jumping, overtaking)
- sounds not played on some slow machines
- no supercars 1 in-game music tracks!: not in mod format but "Ben Daglish" format: no replayer => won't be done!

not confirmed bugs To Do:

- find out why braking is still framerate/update freq. dependent
- CPU spin to fix (if angle too sharp then brake)
- management: add the money BEFORE money-stealing comm screens deduct the money (so it's harder, and faithful)
- sometimes car engine does not stop on game exit


How to start:

- Windows: Just run the Supercars3.bat command file located in the "bin" sub-directory
- Linux/Unix: run the Supercars3.sh command file located in the "bin" sub-directory

Requirements:

- Java Runtime 1.5 or higher (Else you get "cannot find main class, program will exit" message at startup)
  (I recommend 1.5 instead of 1.6 when playing in full-screen mode)
- Windows or Linux (use .bat files on Windows, .sh files on Linux)

Controls:

To play, press SPACE to activate options screen.
Use arrows to select, TAB to switch option pages, RETURN to start the game.

- Arrows+Space or joypad control player 1, numeric pad+0 control player 2
- ESC: quits the game and return to titles.
- P:   pauses the game.
- F1:  screen snapshot
- F2:  full circuit snapshot (mainly for debug purposes, but still...)
- F10: quit the application anytime.
- right arrow: skips to next demo screen (during rolling demo)

Detailed music credits:

- original music modules by Barry Leitch. Includes title & menu modules. Wrongly Spelled Barry Leech in the original game.
- remixed mp3 track (title) by Fabrice Deshayes Aka Xtream
- remixed mp3 track (menu) by Teippi
- "random" in-game music modules by various artists: Shaun Southern (music), Barry Leitch (Lotus II title),
  Patrick Phelan (Lotus III modules), Matrin Iveson (Jaguar XJ-220 modules). Your work is awesome guys.

Notes:

I started this game in december 2004 so it's a long time ago, and was not
able to work on it for at least 1 year because of other projects, a move,
switching jobs, lack of motivation and lack of continuous/uninterruptible
time slots required to solve some pseudo A.I. problems annoying bugs, and
fine tunings required for a good gameplay.
Now I think that the game is pretty complete, apart from the bugs listed above.

The game has GFX problems with JRE 1.6 in full screen mode: cars are not displayed properly
under tunnels (it works in windowed mode!)

If you don't like some in-game music tracks, you can add/remove modules in music/user sub-directory

Thanks go to Tim Wilson for the 3D model of the original SC2 cars. I forgot to credit him earlier. Sorry.
