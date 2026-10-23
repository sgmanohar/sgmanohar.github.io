#!/usr/bin/env python
# convert neurology.txt into json
import json
import re

#input  = '/media/manohar/Tera/Java/eclipse/Apps/source/neurology/Neurology.txt'
#output = '/media/manohar/Tera/Java/WebRoot/neurosim/Neurology.json'
input  = './Neurology.txt'
output = './Neurology.json'
def fliplr_str(s):
  ''' convert all lefts in a string to rights and vice versa '''
  return s.replace("left","~").replace("right","left").replace("~","right")
  
DO_MIRROR = True  
ln    = 0
data  = {}
line_continuation = False
delimiter = re.compile("[\s,]")
with open(input) as f:
  for line in f:
    tokens = [x.strip() for x in delimiter.split(line)]
    if not tokens: continue
    if tokens[0] and tokens[0][0] == '#': 
      continue
    if not line_continuation:
      try:  # if we have a blank first token, skip it
        while not tokens[0]: tokens=tokens[1:]
      except: continue # if we run out of tokens, skip to next line
      if tokens[0][0] == "#":  # could still be a comment on the first token?
        continue # go to next line
      if not tokens[0][-1] == ':': # first token must end in colon (start of new region)
        raise ValueError('strange line %g:%s' % (ln,line))
      
      ln+=1   # we have found a valid new region name
      print( tokens[0] + ' {' )
      current_region_name = tokens[0][:-1] 
      tokens=tokens[1:] # consume first token which is the region name
      if current_region_name in data: # has the region already been created?
        current_region = data[current_region_name] # if so use that object
      else: # otherwise create a new blank region
        current_region  = {}    
        data[current_region_name] = current_region # and store it with the appropriate name
      current_section = None   # section within the region
    line_continuation = False  
    for t in tokens:
      if not t: continue
      if t[0] == '-':
        if current_section is not None:
           print( ' }' )
           current_section = None
        if t[1] == 'i':
           print( ' i: {' )
           current_section = []
           current_region['i'] = current_section
        elif t[1] == 'v':
           print( ' v: {' )
           current_section = []
           current_region['v'] = current_section
        elif t[1] == 'c': # contains
           print( ' c: {' )
           current_section = []
           current_region['c'] = current_section
        elif t[1] == 'm':
           print( ' m:true' )
           current_region['m'] = True # mirrored
        elif t[1] == 's': # synonyms
           print( ' s: {' )
           current_section = []
           current_region['s'] = current_section
        elif t[1] == 'h':
           print( ' h:true' ) 
           current_region['h'] = True # hidden
        else:
           raise ValueError('unknown token %s in %s' % (t, line))
      elif t == '\\':
        print( "CONT!" )
        line_continuation = True
      else: # not '-' option       
        print( "  "+t ) # just add an item to the current section
        if current_section is None:
          raise ValueError('unexpected token %s in %s' % (t,current_region_name))
        current_section.append(t)
        if t not in data: # ensure item exists
          data[t] = {}
    if DO_MIRROR and ('m' in current_region):
      newitem = {}
      # copy everthing across, flipping l/r
      for k in current_region: # for each section, 
        if isinstance(current_region[k],list): # list of items?
          newitem[k] = [fliplr_str(x) for x in current_region[k]]
          for i in newitem[k]:
            if i not in data: 
              data[i] = {} # ensure item exist
              print( "mirroring %s" % (i) )
        elif isinstance(current_region[k],str):
          newitem[k] = fliplr_str(current_region[k])
        elif k != "m":
          newitem[k] = current_region[k]
      data[fliplr_str(current_region_name)] = newitem
with open(output, 'w') as outfile:
    json.dump(data, outfile)
    

  