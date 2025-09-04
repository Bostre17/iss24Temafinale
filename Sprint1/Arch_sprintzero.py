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
with Diagram('sprintzeroArch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
### see https://renenyffenegger.ch/notes/tools/Graphviz/attributes/label/HTML-like/index
     with Cluster('ctxwis', graph_attr=nodeattr):
          incinerator=Custom('incinerator','./qakicons/symActorSmall.png')
          oprobot=Custom('oprobot','./qakicons/symActorSmall.png')
          wis=Custom('wis','./qakicons/symActorSmall.png')
     with Cluster('ctxscale', graph_attr=nodeattr):
          scale=Custom('scale','./qakicons/symActorSmall.png')
     with Cluster('ctxsonar', graph_attr=nodeattr):
          sonar=Custom('sonar','./qakicons/symActorSmall.png')
     with Cluster('ctxbasicrobot', graph_attr=nodeattr):
          basicrobot=Custom('basicrobot(ext)','./qakicons/externalQActor.png')
     incinerator >> Edge( label='burnEnd', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     incinerator >> Edge( label='burnEnd', **eventedgeattr, decorate='true', fontcolor='red') >> wis
     scale >> Edge( label='stateScale', **eventedgeattr, decorate='true', fontcolor='red') >> wis
     sonar >> Edge( label='stateSonar', **eventedgeattr, decorate='true', fontcolor='red') >> wis
     oprobot >> Edge( label='position', **eventedgeattr, decorate='true', fontcolor='red') >> wis
     sys >> Edge( label='stateScale', **evattr, decorate='true', fontcolor='darkgreen') >> wis
     sys >> Edge( label='stateSonar', **evattr, decorate='true', fontcolor='darkgreen') >> wis
     sys >> Edge( label='burnEnd', **evattr, decorate='true', fontcolor='darkgreen') >> wis
     oprobot >> Edge(color='magenta', style='solid', decorate='true', label='<engage<font color="darkgreen"> engagedone engagerefused</font> &nbsp; moverobot<font color="darkgreen"> moverobotdone moverobotfailed</font> &nbsp; >',  fontcolor='magenta') >> basicrobot
     oprobot >> Edge(color='blue', style='solid',  decorate='true', label='<ashDeposited &nbsp; >',  fontcolor='blue') >> sonar
     oprobot >> Edge(color='blue', style='solid',  decorate='true', label='<atIncinerator &nbsp; >',  fontcolor='blue') >> wis
     wis >> Edge(color='blue', style='solid',  decorate='true', label='<act &nbsp; >',  fontcolor='blue') >> incinerator
     oprobot >> Edge(color='blue', style='solid',  decorate='true', label='<rpTaken &nbsp; >',  fontcolor='blue') >> scale
     wis >> Edge(color='blue', style='solid',  decorate='true', label='<goHome &nbsp; bringRP &nbsp; bringAsh &nbsp; >',  fontcolor='blue') >> oprobot
diag
