### conda install diagrams
from diagrams import Cluster, Diagram, Edge
from diagrams.custom import Custom
import os
os.environ['PATH'] += os.pathsep + 'C:/Program Files/Graphviz/bin/'

graphattr = {     #https://www.graphviz.org/doc/info/attrs.html
    'fontsize': '22',
}

nodeattr = {   
    'fontsize': '22',
    'bgcolor': 'lightyellow'
}

eventedgeattr = {
    'color': 'red',
    'style': 'dotted'
}
evattr = {
    'color': 'darkgreen',
    'style': 'dotted'
}
with Diagram('sprint2Arch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
### see https://renenyffenegger.ch/notes/tools/Graphviz/attributes/label/HTML-like/index
     with Cluster('ctxwis', graph_attr=nodeattr):
          incenerator=Custom('incenerator','./qakicons/symActorSmall.png')
          oprobot=Custom('oprobot','./qakicons/symActorSmall.png')
          wis=Custom('wis','./qakicons/symActorSmall.png')
          scale=Custom('scale','./qakicons/symActorSmall.png')
          tester=Custom('tester','./qakicons/symActorSmall.png')
     with Cluster('ctxddr', graph_attr=nodeattr):
          basicrobot=Custom('basicrobot(ext)','./qakicons/externalQActor.png')
     with Cluster('ctxmd', graph_attr=nodeattr):
          sonar24=Custom('sonar24(ext)','./qakicons/externalQActor.png')
     incenerator >> Edge( label='burnEnd', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     incenerator >> Edge( label='burnEnd', **eventedgeattr, decorate='true', fontcolor='red') >> wis
     scale >> Edge( label='updateWS', **eventedgeattr, decorate='true', fontcolor='red') >> wis
     oprobot >> Edge( label='updateStateRobot', **eventedgeattr, decorate='true', fontcolor='red') >> wis
     sys >> Edge( label='updateWS', **evattr, decorate='true', fontcolor='darkgreen') >> wis
     sys >> Edge( label='updateAS', **evattr, decorate='true', fontcolor='darkgreen') >> wis
     sys >> Edge( label='burnEnd', **evattr, decorate='true', fontcolor='darkgreen') >> wis
     sys >> Edge( label='updateAS', **evattr, decorate='true', fontcolor='darkgreen') >> tester
     oprobot >> Edge(color='magenta', style='solid', decorate='true', label='<engage<font color="darkgreen"> engagedone engagerefused</font> &nbsp; moverobot<font color="darkgreen"> moverobotdone moverobotfailed</font> &nbsp; >',  fontcolor='magenta') >> basicrobot
     wis >> Edge(color='magenta', style='solid', decorate='true', label='<cmd &nbsp; >',  fontcolor='magenta') >> incenerator
     oprobot >> Edge(color='blue', style='solid',  decorate='true', label='<endRoutine &nbsp; >',  fontcolor='blue') >> tester
     tester >> Edge(color='blue', style='solid',  decorate='true', label='<newRP &nbsp; >',  fontcolor='blue') >> scale
     oprobot >> Edge(color='blue', style='solid',  decorate='true', label='<robotArrived &nbsp; endRoutine &nbsp; >',  fontcolor='blue') >> wis
     wis >> Edge(color='blue', style='solid',  decorate='true', label='<sonarstart &nbsp; >',  fontcolor='blue') >> sonar24
     oprobot >> Edge(color='blue', style='solid',  decorate='true', label='<arrivoRobot &nbsp; >',  fontcolor='blue') >> scale
     wis >> Edge(color='blue', style='solid',  decorate='true', label='<burn &nbsp; >',  fontcolor='blue') >> incenerator
     wis >> Edge(color='blue', style='solid',  decorate='true', label='<goWasteIn &nbsp; goBurnOut &nbsp; goHome &nbsp; >',  fontcolor='blue') >> oprobot
diag
