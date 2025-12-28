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
          sonardevicemock=Custom('sonardevicemock','./qakicons/symActorSmall.png')
          warningdevice=Custom('warningdevice','./qakicons/symActorSmall.png')
          datacleaner=Custom('datacleaner','./qakicons/symActorSmall.png')
          monitoringdevice=Custom('monitoringdevice','./qakicons/symActorSmall.png')
     sonardevicemock >> Edge( label='sonardevicedata', **eventedgeattr, decorate='true', fontcolor='red') >> datacleaner
     datacleaner >> Edge( label='sonardatacleaner', **eventedgeattr, decorate='true', fontcolor='red') >> monitoringdevice
     monitoringdevice >> Edge( label='stateSonar', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     monitoringdevice >> Edge(color='blue', style='solid',  decorate='true', label='<sonarstop &nbsp; sonarstart &nbsp; >',  fontcolor='blue') >> sonardevicemock
diag
