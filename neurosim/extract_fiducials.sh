(
 # main fiducials object 
 echo "\"fiducials\":{"
 # find svg files in the nested subdirectories
 for x in `ls slices/*/*/*/*.svg` ; do
  # print the filename as the property key
  echo "  \"$x\":["
  # and the value is an array of 3 arrays, each one [x,y]
  cat "$x" | xpath -e "//path[starts-with(@id,'mni')]/@d" -q | awk '{print "    ["$2"],"}'
  echo "  ],"
 done
 echo "}"
)  | perl -0777 -pe 's/,(\s*})/\1/g' | perl -0777 -pe 's/,(\s*])/\1/g'
# finally get rid of extra commas at the ends of lists

