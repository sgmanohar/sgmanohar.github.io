package medicine;

public class Essay {
  Entity e;
  public Essay(Entity e){
    this.e=e;
  }
  String text="";
  public String getText(){
    text=e.name;
    if(e.synonyms.size()>0){
      text+=", also known as";
      for(int i=0;i<e.synonyms.size();i++){
        text+=" "+e.synonyms.elementAt(i).toString()+",";
      }
    }

    Entity p = Entities.getUltimateParents(e);
    if(p==null)    text+=" is something";
    else text+=" is a "+p.name;



    return text;
  }
}
