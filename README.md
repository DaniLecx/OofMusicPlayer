# Oof Music Player

Oof Music Player is an Android app for listening to popular songs remixed with the Oof sound from Roblox.

![Oof Music Player App](https://i.imgur.com/tqelMz8l.jpg)

It has all the features of a basic music player:
- Shuffle
- Next / Previous
- Play / Pause
- Loop song / Loop playlist

The app downloads the music once from [/assets](https://github.com/DaniLecx/OofMusicPlayer/tree/master/assets) folder and stores it in the internal storage.

When offline the songs stored in the internal storage are displayed.

## How to 

### Convert midi file to Oof remixed mp3

#### Setup

Install [SynthFont1](http://www.synthfont.com/Downloads.html)

#### Files
You will find everything you need in [/MIDItoMP3](https://github.com/DaniLecx/OofMusicPlayer/tree/master/MIDItoMP3) folder.

- [/midi](https://github.com/DaniLecx/OofMusicPlayer/tree/master/MIDItoMP3/midi) folder contains a few **midi files** from popular songs
- *oof.sf2* is the **sound font**, it contains all the different Oof notes
- *oof.wav* is the **sound** from which the soundfont was made

#### SynthFont

- Set *oof.sf2* as default sound file 

  **File->Set default Soundfont file**

- Open the midi file to convert 

  **File->Open MIDI**
- Listen or convert to MP3 

  **Play to Speakers / File**

![SynthFont play or record buttons](https://i.imgur.com/PXDWJeY.png)