Read me 

### Running Test Harness
1. Go to "Harness.java" and find device name. Type the device name as argument and run file.
2. Run specific device (e.g., "Display.java")

### Font Information
To use any font, font size, or color, use it's corresponding order in the associated CSV from ranges 0 to 2.

_Header_: Font 0, Size 0, Color 0<br>
_Body Text_: Font 1, Size 1, Color 1<br>
_Something_: Font 2, Size 2, Color 2<br>

#### Fonts
1. Impact
2. Verdana
3. Constantia
#### Font Sizes
1. 30
2. 19
3. 22
#### Font Colors
1. Green, #84E296
2. Blue, #083D77 
3. Indian red, #EB5160

### Message Formatting:

- Buttons
  - Example: b:n:t
    - where n is the button number
    - t is x for exclusive and m for multiple
  

- Text
  - Example: t:n[n]:sx:fy:cz:m
      - where n[n] is the text number
      - x is the size to use, y is the font to use
      - z is the color to use, and m is the message

- Notes:
  - Text number, size, etc. are from 0-2