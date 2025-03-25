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
with Diagram('incineratorArch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
### see https://renenyffenegger.ch/notes/tools/Graphviz/attributes/label/HTML-like/index
     with Cluster('ctxincinerator', graph_attr=nodeattr):
          incinerator=Custom('incinerator','./qakicons/symActorSmall.png')
     sys >> Edge( label='startUp', **evattr, decorate='true', fontcolor='darkgreen') >> incinerator
     sys >> Edge( label='startBurning', **evattr, decorate='true', fontcolor='darkgreen') >> incinerator
     incinerator >> Edge( label='burning', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     incinerator >> Edge( label='finishedBurning', **eventedgeattr, decorate='true', fontcolor='red') >> sys
     incinerator >> Edge( label='burnEnd', **eventedgeattr, decorate='true', fontcolor='red') >> sys
diag
