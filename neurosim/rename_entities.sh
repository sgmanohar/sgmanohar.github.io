#!/bin/bash 
# rename.sh

#1
#from='posterior-limb-internal-capsule'
#to='internal-capsule-posterior-limb'

#2
#from='pyramid-of-medulla'
#to='pyramids-of-medulla'

#3
#from='mammillary'
#to='mamillary'

#4
#from='basal-optic-nucleus-of-meynert'
#to='basal-nucleus-of-meynert'

#5
#from='fornix-fimbria'
#to='fimbria-fornix'

#6
#from='reticular-thalamic-nucleus'
#to='reticular-nucleus-thalamus'

#7
#from='centromedial-thalamic-nucleus'
#to='centromedian-thalamic-nucleus'

#8
#from='centromedian-nucleus'
#to='centromedian-thalamic-nucleus'

#9
#from='anterior-cerebellar-lobe'
#to='cerebellar-anterior-lobe'

#10
#from='lateral-geniculate'
#to='lateral-geniculate-nucleus'
#10.5
#from='nucleus-nucleus'
#to='nucleus'

#11
#from='coronal-radiata'
#to='corona-radiata'

#12
#from='lateral-posterior-nucleus'
#to='lateral-posterior-thalamic-nucleus'

#13
#from=dorsolateral-thalamus
#to=lateral-dorsal-thalamus

#14
#from=right-dorsal-motor-nucleus-of-vagus-nerve
#to=dorsal-motor-nucleus-of-vagus-nerve-right
#14.1
#from=left-dorsal-motor-nucleus-of-vagus-nerve
#to=dorsal-motor-nucleus-of-vagus-nerve-left

#15
from=pulivinar
to=pulvinar


files=$(find -iname '*.svg' -exec grep -l "$from" {} \;)
echo "$files"

while read -r line; do
  sed -i.old "s/$from/$to/g"  "$line"
done <<< "$files"
