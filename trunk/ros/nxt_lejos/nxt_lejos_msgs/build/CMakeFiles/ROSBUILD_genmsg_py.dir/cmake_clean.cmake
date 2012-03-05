FILE(REMOVE_RECURSE
  "../src/nxt_lejos_msgs/msg"
  "../msg_gen"
  "../msg_gen"
  "CMakeFiles/ROSBUILD_genmsg_py"
  "../src/nxt_lejos_msgs/msg/__init__.py"
  "../src/nxt_lejos_msgs/msg/_Compass.py"
  "../src/nxt_lejos_msgs/msg/_JointVelocity.py"
  "../src/nxt_lejos_msgs/msg/_Decibels.py"
  "../src/nxt_lejos_msgs/msg/_JointPosition.py"
  "../src/nxt_lejos_msgs/msg/_DNSCommand.py"
  "../src/nxt_lejos_msgs/msg/_Battery.py"
  "../src/nxt_lejos_msgs/msg/_Tone.py"
)

# Per-language clean rules from dependency scanning.
FOREACH(lang)
  INCLUDE(CMakeFiles/ROSBUILD_genmsg_py.dir/cmake_clean_${lang}.cmake OPTIONAL)
ENDFOREACH(lang)
