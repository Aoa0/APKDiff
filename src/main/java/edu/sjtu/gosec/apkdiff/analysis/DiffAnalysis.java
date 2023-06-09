package edu.sjtu.gosec.apkdiff.analysis;

import edu.sjtu.gosec.apkdiff.profile.AppProfile;
import edu.sjtu.gosec.apkdiff.profile.ClassProfile;
import edu.sjtu.gosec.apkdiff.util.HierarchyNode;
import edu.sjtu.gosec.apkdiff.util.HierarchyTree;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

public class DiffAnalysis {

    private final AppProfile srcProfile;
    private final AppProfile tarProfile;
    private final HierarchyTree srcTree;
    private final HierarchyTree tarTree;
    private Map<String, ClassProfile> SourceMatched;
    private Map<String, ClassProfile> TargetMatched;
    private Map<String, String> matches;
    private Graph<String, DefaultEdge> potentialMatches;
    private Map<HierarchyNode, HierarchyNode> packageMatches;

    public DiffAnalysis(AppProfile src, AppProfile tar) {
        this.matches = new HashMap<>();
        this.srcProfile = src;
        this.tarProfile = tar;
        this.srcTree = src.hierarchyTree;
        this.tarTree = tar.hierarchyTree;
        this.SourceMatched = new HashMap<>();
        this.TargetMatched = new HashMap<>();
        this.potentialMatches = new DefaultDirectedGraph<>(DefaultEdge.class);
    }

    public void diff() {
        buildPackageMatch();
        basicMatch();
        boolean sign = true;
        while(sign) {
            System.out.println("iterate");
            sign = extendMatch();
        }

        show();
    }

    private void basicMatch() {
        for (Map.Entry<HierarchyNode, HierarchyNode> entry : packageMatches.entrySet()) {
            MatchSet matchSet = new MatchSet(entry.getKey(), entry.getValue());
            matchSet.runMatch();
            check();
        }
    }

    class MatchSet {
        private final Set<ClassProfile> ClassSet1;
        private final Set<ClassProfile> ClassSet2;

        private MatchSet(HierarchyNode n1, HierarchyNode n2) {
            ClassSet1 = n1.getAllClasses();
            ClassSet2 = n2.getAllClasses();
            ClassSet1.removeAll(SourceMatched.values());
            ClassSet2.removeAll(TargetMatched.values());
        }

        private void runMatch() {
            for (ClassProfile c1 : ClassSet1) {
                for (ClassProfile c2 : ClassSet2) {
                    ClassComparator comparator = new ClassComparator(c1, c2);
                    if (comparator.similar()) {
                        addPotentialMatch(c1, c2);
                    }
                }
            }
        }

        private void addPotentialMatch(ClassProfile c1, ClassProfile c2) {
            String vertex1 = "source." + c1.getName();
            String vertex2 = "target." + c2.getName();
            potentialMatches.addVertex(vertex1);
            potentialMatches.addVertex(vertex2);
            potentialMatches.addEdge(vertex1, vertex2);
        }
    }

    private boolean check() {
        Map<String, String> newMatch = new HashMap<>();

        for (String vertex : potentialMatches.vertexSet()) {
            if (vertex.startsWith("target.")) {
                continue;
            }
            if (potentialMatches.edgesOf(vertex).size() == 1) {
                DefaultEdge edge = potentialMatches.edgesOf(vertex).iterator().next();
                String vertex2 = potentialMatches.getEdgeTarget(edge);
                if (potentialMatches.edgesOf(vertex2).size() == 1) {
                    newMatch.put(vertex, vertex2);
                }
            }
        }

        return refreshMatch(newMatch);
    }

    private void addMatch(String c1, String c2) {
        matches.put(c1, c2);
        //System.out.println(c1 + " -> " + c2);
        SourceMatched.put(c1, srcProfile.getAllClasses().get(c1));
        TargetMatched.put(c2, tarProfile.getAllClasses().get(c2));
    }

    private boolean extendMatch() {
        Map<String, String> newMatch = new HashMap<>();
        for(DefaultEdge edge:potentialMatches.edgeSet()) {
            String src = potentialMatches.getEdgeSource(edge);
            String tar = potentialMatches.getEdgeTarget(edge);
            ClassProfile srcClass = srcProfile.getAllClasses().get(src.replace("source.", ""));
            ClassProfile tarClass = tarProfile.getAllClasses().get(tar.replace("target.", ""));
            if(matches.containsKey(srcClass.getSuperClass())) {
                if (Objects.equals(matches.get(srcClass.getSuperClass()), tarClass.getSuperClass())) {
                    newMatch.put(src, tar);
                }
            }
        }
        refreshMatch(newMatch);


        return check();
    }

    private boolean refreshMatch(Map<String, String> newMatch) {
        boolean sign = false;
        for (Map.Entry<String, String> entry : newMatch.entrySet()) {
            sign = true;
            potentialMatches.removeVertex(entry.getKey());
            potentialMatches.removeVertex(entry.getValue());
            addMatch(entry.getKey().replace("source.", ""), entry.getValue().replace("target.", ""));
        }
        return sign;
    }
    private void buildPackageMatch() {
        this.packageMatches = new LinkedHashMap<>();
        HierarchyNode i1 = srcTree.root;
        HierarchyNode i2 = tarTree.root;
        BuildPackageMatch(i1, i2);
        this.packageMatches.put(srcTree.root, tarTree.root);
    }

    private void BuildPackageMatch(HierarchyNode i1, HierarchyNode i2) {
        for (String name : i1.getChild().keySet()) {
            if (i2.hasChild(name)) {
                BuildPackageMatch(i1.getChild(name), i2.getChild(name));
                this.packageMatches.put(i1.getChild(name), i2.getChild(name));
            }
        }
    }

    public void show() {
        //ToDo: Show the result
    }

    public Map<String, String> getResult() {
        return matches;
    }
}
