#!/usr/bin/env python3
# create regions_by_slice:{}
import json
import re
import os
import lxml.etree

FOLDER = 'slices'
output = 'SliceMetadata_regions_by_slice.json'
result = {}

find_id = re.compile('id="([^"]*)"', re.MULTILINE | re.IGNORECASE)

count = 0
for root, subdirs, files in os.walk(FOLDER):
  for filename in files:
    if filename.endswith(".svg") and not " " in filename:
      filepath = os.path.join(root, filename)
      print(filepath)
      with open(filepath, 'r') as f:
        result[filepath] = []
        list = result[filepath]
        doc = lxml.etree.parse(f)
        # find paths
        ns = {'svg':"http://www.w3.org/2000/svg"}
        for id in doc.xpath('//svg:path/@id',namespaces=ns):
          #id = n.attrib.get("id")
          if id != None and not id.startswith("path") and not id.startswith("mni_"):
            list+=[id]
            count += 1
        # now find groups
        for id in doc.xpath('//svg:g/@id',namespaces=ns):
          #id = n.attrib.get("id")
          if id != None and not re.match('g[0-9]+',id) and not id.startswith("mni_"):
            list+=[id]
            count += 1

with open(output,'w') as out:
  json.dump(result, out, indent=2)  
print(count)  