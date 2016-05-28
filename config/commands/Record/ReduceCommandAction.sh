#!/bin/sh

echo "the reduction for Record will complete."
rm ~/partial_vids/complete/grid.mkv

~/ffmpeg_install/ffmpeg-2.8/ffmpeg \
  -i ~/partial_vids/video.101.h264 \
  -i ~/partial_vids/video.102.h264 \
  -filter_complex " \
    nullsrc=size=1280x480 [base]; \
    [0:v] setpts=PTS-STARTPTS, scale=640x480 [upperleft]; \
    [1:v] setpts=PTS-STARTPTS, scale=640x480 [upperright]; \
    [base][upperleft] overlay=shortest=1 [tmp1]; \
    [tmp1][upperright] overlay=shortest=1:x=640 \
  " \
  -c:v libx264 ~/partial_vids/complete/grid.mkv
echo "Reduction complete. Playing the combined video"

mplayer ~/partial_vids/complete/grid.mkv

