#!/bin/bash
cd /home/abhay/repos/thewardenprotocol/docs/storelisting

# Inputs with looping - 2 seconds each
INPUTS="-loop 1 -t 2 -i feature_graphic_169.png \
-loop 1 -t 2 -i promo_1.png \
-loop 1 -t 2 -i promo_2.png \
-loop 1 -t 2 -i promo_3.png \
-loop 1 -t 2 -i promo_4.png \
-loop 1 -t 2 -i promo_5.png \
-loop 1 -t 2 -i promo_6.png \
-loop 1 -t 2 -i promo_7.png \
-loop 1 -t 2 -i promo_8.png"

# Filter complex for scaling and transitions
FILTER=""
for i in {0..8}; do
    FILTER+="[$i:v]scale=1280:720,framerate=30,settb=1/30,format=rgb24[v$i]; "
done

# duration 0.5, offset every 1.5s
FILTER+="[v0][v1]xfade=transition=slideleft:duration=0.5:offset=1.5[x1]; "
FILTER+="[x1][v2]xfade=transition=slideleft:duration=0.5:offset=3.0[x2]; "
FILTER+="[x2][v3]xfade=transition=slideleft:duration=0.5:offset=4.5[x3]; "
FILTER+="[x3][v4]xfade=transition=slideleft:duration=0.5:offset=6.0[x4]; "
FILTER+="[x4][v5]xfade=transition=slideleft:duration=0.5:offset=7.5[x5]; "
FILTER+="[x5][v6]xfade=transition=slideleft:duration=0.5:offset=9.0[x6]; "
FILTER+="[x6][v7]xfade=transition=slideleft:duration=0.5:offset=10.5[x7]; "
FILTER+="[x7][v8]xfade=transition=slideleft:duration=0.5:offset=12.0[x8]; "

# Generate palette and map it for high quality GIF with max 128 colors to save size
FILTER+="[x8]split[s0][s1];[s0]palettegen=stats_mode=diff:max_colors=32[p];[s1][p]paletteuse=dither=none"

# Run FFmpeg
ffmpeg -y $INPUTS -filter_complex "$FILTER" -c:v gif promo.gif

echo "Done"
ls -lh promo.gif
