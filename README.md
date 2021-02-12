# Low Level Computer Emulator
This is an emulator that allows for each component to be individually configured.
The following components are each emulated and configurable.

* Busses
* Mappers (TODO)
    * Bank switching (TODO)
    * Address re-mapping (TODO)
    * Address mirroring (TODO)
* Clocks
* Flags/Interrupts
* ROM
* Cartridges (TODO)
* RAM (TODO)
* Processors
    * NMOS 6502 (In Progress)
    * NMOS 6510/8500 (TODO)
    * Zilog Z80 (TODO)
    * Motorola 68000 (TODO)
* Graphics (TODO)
    * NES PPU (TODO)
    * SEGA 315-5316 VDP / YM7101
* Audio Processing (TODO)
* Other IO
    * Joysticks
    * Keyboards
    * GPIO


## Aims of the project
The aim of this project is to learn about computer architecture along the way. 
The plan is to be able to construct a variety of different computers by way of config files, such as NES, C64, Apple II
The main example following is that of developing a Ninenteno NES system in software following various guides.

I have started with the NMOS 6502 CPU due to its very simple instruction set, so it seemed like a good place to get started.

## Resources
### NMOS 6502
* [Emulator 101 6502](http://www.emulator101.com/6502-emulator.html "Emulator 101 6502")
* [http://www.obelisk.me.uk/ 6502](http://www.obelisk.me.uk/6502/ "http://www.obelisk.me.uk/")
* [https://www.masswerk.at/6502/6502_instruction_set.html](https://www.masswerk.at/6502/6502_instruction_set.html "https://www.masswerk.at/6502/6502_instruction_set.html")
* [Youtube One Lone Coder](https://www.youtube.com/watch?v=8XmxKPJDGU0 "One Lone Coder")
