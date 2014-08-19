package com.github.linsolas.casperjsrunner;

import static java.io.File.separator;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

public class OrdererScriptsFinderDecorator implements ScriptsFinder {

    private final ScriptsFinder innerFinder;

    public OrdererScriptsFinderDecorator(final ScriptsFinder innerFinder) {
        this.innerFinder = innerFinder;
    }

    @Override
    public Collection<String> findScripts() {
        final TreeSet<String> result = new TreeSet<String>(new Comparator<String>() {
            @Override
            public int compare(final String o1, final String o2) {
                if (!o1.contains(separator) && o2.contains(separator)) {
                    return -1;
                } else if (o1.contains(separator) && !o2.contains(separator)) {
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
