package com.github.linsolas.casperjsrunner;

import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

public class OrdererScriptsFinderDecorator implements ScriptsFinder {

    private ScriptsFinder innerFinder;

    public OrdererScriptsFinderDecorator(ScriptsFinder innerFinder) {
        this.innerFinder = innerFinder;
    }

    @Override
    public Collection<String> findScripts() {
        TreeSet<String> result = new TreeSet<String>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (!o1.contains(File.separator) && o2.contains(File.separator)) {
                    return -1;
                } else if (o1.contains(File.separator) && !o2.contains(File.separator)) {
                    return 1;
                } else {
                    return o1.compareTo(o2);
                }
            }
        });
        result.addAll(innerFinder.findScripts());
        return result;
    }

}
