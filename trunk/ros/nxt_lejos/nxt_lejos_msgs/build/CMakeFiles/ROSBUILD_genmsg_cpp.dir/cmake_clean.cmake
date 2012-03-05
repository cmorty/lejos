FILE(REMOVE_RECURSE
  "../src/nxt_lejos_msgs/msg"
  "../msg_gen"
  "../msg_gen"
  "CMakeFiles/ROSBUILD_genmsg_cpp"
  "../msg_gen/cpp/include/nxt_lejos_msgs/Compass.h"
  "../msg_gen/cpp/include/nxt_lejos_msgs/JointVelocity.h"
  "../msg_gen/cpp/include/nxt_lejos_msgs/Decibels.h"
  "../msg_gen/cpp/include/nxt_lejos_msgs/JointPosition.h"
  "../msg_gen/cpp/include/nxt_lejos_msgs/DNSCommand.h"
  "../msg_gen/cpp/include/nxt_lejos_msgs/Battery.h"
  "../msg_gen/cpp/include/nxt_lejos_msgs/Tone.h"
)

# Per-language clean rules from dependency scanning.
FOREACH(lang)
  INCLUDE(CMakeFiles/ROSBUILD_genmsg_cpp.dir/cmake_clean_${lang}.cmake OPTIONAL)
ENDFOREACH(lang)
