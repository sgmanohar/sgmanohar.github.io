package medicine;

import java.util.*;

public class EntityData {
  private Hashtable namesToEntities=new Hashtable();
  public EntityData() {
  }
  public Entity addNewEntity(String name){
    Entity e=new Entity(null,0);
    e.name=name;
    Object o=namesToEntities.put(name,e);
    if(o!=null) throw new IllegalStateException("Two entites with key "+name);
    return e;
  }
  public int size(){return namesToEntities.size();}
  public Entity findEntityExact(String name){
    return (Entity)namesToEntities.get(name);
  }
  public Vector findEntities(String text,boolean contains, boolean csensitive){
    Vector res = new Vector();
    for(Enumeration k=namesToEntities.elements();k.hasMoreElements();){
      Entity c=(Entity)k.nextElement();
      String s=c.name;
      String textlc=text.toLowerCase();
      if(contains){
        if(csensitive){
          if (s.indexOf(text)>=0) {res.addElement(namesToEntities.get(s));continue;}
        }else{
          if (s.toLowerCase().indexOf(textlc) >= 0) {
            res.addElement(namesToEntities.get(s));
            continue;
          }
        }
        for(int i=0;i<c.synonyms.size();i++){
          String syn=(String)c.synonyms.elementAt(i);
          if(csensitive){
            if (syn.indexOf(text)>=0) {res.addElement(namesToEntities.get(s));break;}
          }else{
            if (syn.toLowerCase().indexOf(textlc) >= 0) {
              res.addElement(namesToEntities.get(s));
              break;
            }
          }
        }
      }else{
        if(csensitive){
          if (s.equals(text)) res.addElement(namesToEntities.get(s));
        }else{
          if (s.equalsIgnoreCase(text)) res.addElement(namesToEntities.get(s));
        }
      }
    }
    return res;
  }

  public Entity getFirstEntity() {
    return (Entity)namesToEntities.values().iterator().next();
  }
}
