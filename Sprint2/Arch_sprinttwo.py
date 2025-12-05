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
with Diagram('sprinttwoArch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
### see https://renenyffenegger.ch/notes/tools/Graphviz/attributes/label/HTML-like/index
     with Cluster('ctxwis', graph_attr=nodeattr):
          incinerator=Custom('incinerator','./qakicons/symActorSmall.png')
          scale=Custom('scale','./qakicons/symActorSmall.png')
          oprobot=Custom('oprobot','./qakicons/symActorSmall.png')
          wis=Custom('wis','./qakicons/symActorSmall.png')
     with Cluster('ctxbasicrobot', graph_attr=nodeattr):
          basicrobot=Custom('basicrobot(ext)','./qakicons/externalQActor.png')
     with Cluster('ctxmd', graph_attr=nodeattr):
          monitoringdevice=Custom('monitoringdevice(ext)','./qakicons/externalQActor.png')
     incinerator >> Edge( label='burnEnd', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     incinerator >> Edge( label='burnEnd', **eventedgeattr, decorate='true', fontcolor='red') >> wis
     scale >> Edge( label='stateScale', **eventedgeattr, decorate='true', fontcolor='red') >> wis
     sys >> Edge( label='stateSonar', **evattr, decorate='true', fontcolor='darkgreen') >> wis
     wis >> Edge( label='stateLed', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     sys >> Edge( label='burnEnd', **evattr, decorate='true', fontcolor='darkgreen') >> wis
     oprobot >> Edge(color='magenta', style='solid', decorate='true', label='<engage<font color="darkgreen"> engagedone engagerefused</font> &nbsp; moverobot<font color="darkgreen"> moverobotdone moverobotfailed</font> &nbsp; >',  fontcolor='magenta') >> basicrobot
     wis >> Edge(color='magenta', style='solid', decorate='true', label='<bringRP<font color="darkgreen"> atIncinerator</font> &nbsp; bringAsh<font color="darkgreen"> ashDeposited</font> &nbsp; >',  fontcolor='magenta') >> oprobot
     wis >> Edge(color='blue', style='solid',  decorate='true', label='<sonarstart &nbsp; >',  fontcolor='blue') >> monitoringdevice
     wis >> Edge(color='blue', style='solid',  decorate='true', label='<act &nbsp; notifyRp &nbsp; >',  fontcolor='blue') >> incinerator
     oprobot >> Edge(color='blue', style='solid',  decorate='true', label='<rpTaken &nbsp; >',  fontcolor='blue') >> scale
     wis >> Edge(color='blue', style='solid',  decorate='true', label='<goHome &nbsp; >',  fontcolor='blue') >> oprobot
diag
