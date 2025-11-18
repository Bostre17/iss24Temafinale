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
with Diagram('sonarqak24Arch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
### see https://renenyffenegger.ch/notes/tools/Graphviz/attributes/label/HTML-like/index
     with Cluster('ctxsonarqak24', graph_attr=nodeattr):
          sonar24=Custom('sonar24','./qakicons/symActorSmall.png')
          datacleaner=Custom('datacleaner','./qakicons/symActorSmall.png')
          sonardevice=Custom('sonardevice','./qakicons/symActorSmall.png')
     with Cluster('ctxwis', graph_attr=nodeattr):
          wis=Custom('wis(ext)','./qakicons/externalQActor.png')
     datacleaner >> Edge( label='sonardata', **eventedgeattr, decorate='true', fontcolor='red') >> sonar24
     sonar24 >> Edge( label='updateAS', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     sonardevice >> Edge( label='sonardata', **eventedgeattr, decorate='true', fontcolor='red') >> datacleaner
     sonar24 >> Edge(color='blue', style='solid',  decorate='true', label='<sonarstop &nbsp; sonarstart &nbsp; >',  fontcolor='blue') >> sonardevice
diag
