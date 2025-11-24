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
with Diagram('monitoringdeviceArch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
### see https://renenyffenegger.ch/notes/tools/Graphviz/attributes/label/HTML-like/index
     with Cluster('ctxmd', graph_attr=nodeattr):
          monitoringdevice=Custom('monitoringdevice','./qakicons/symActorSmall.png')
          datacleaner=Custom('datacleaner','./qakicons/symActorSmall.png')
          sonardevice=Custom('sonardevice','./qakicons/symActorSmall.png')
          warningdevice=Custom('warningdevice','./qakicons/symActorSmall.png')
     with Cluster('ctxwis', graph_attr=nodeattr):
          wis=Custom('wis(ext)','./qakicons/externalQActor.png')
     datacleaner >> Edge( label='sonardata', **eventedgeattr, decorate='true', fontcolor='red') >> monitoringdevice
     monitoringdevice >> Edge( label='stateSonar', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     sonardevice >> Edge( label='sonardata', **eventedgeattr, decorate='true', fontcolor='red') >> datacleaner
     sonardevice >> Edge( label='sonardata', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     sys >> Edge( label='stateLed', **evattr, decorate='true', fontcolor='darkgreen') >> warningdevice
     monitoringdevice >> Edge(color='blue', style='solid',  decorate='true', label='<sonarstop &nbsp; sonarstart &nbsp; >',  fontcolor='blue') >> sonardevice
diag
