package net.sourceforge.pmd.dcpd;

import net.jini.space.JavaSpace;
import net.jini.core.lease.Lease;
import net.jini.core.discovery.LookupLocator;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceMatches;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.sourceforge.pmd.cpd.*;

import java.rmi.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class Test {

    private TokenSets tokenSets = new TokenSets();

    public Test() {
        try {
            JavaSpace space = Util.findSpace("mordor");
            add("C:\\j2sdk1.4.0_01\\src\\java\\lang\\", true);
            Entry wrapper = new TokenSetsWrapper(tokenSets);

            long start = System.currentTimeMillis();
            System.out.println("WRITING");
            space.write(wrapper, null, Lease.FOREVER);
            long stop = System.currentTimeMillis();
            System.out.println("that took " + (stop - start) + " milliseconds");

            start = System.currentTimeMillis();
            System.out.println("TAKING");
            TokenSetsWrapper result = (TokenSetsWrapper)space.take(new TokenSetsWrapper(), null, Long.MAX_VALUE);
            stop = System.currentTimeMillis();
            System.out.println("that took " + (stop - start) + " milliseconds");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void add(String dir, boolean recurse) throws IOException {
        FileFinder finder = new FileFinder();
        List files = finder.findFilesFrom(dir, new JavaFileOrDirectoryFilter(), recurse);
        for (Iterator i = files.iterator(); i.hasNext();) {
            add(files.size(), (File)i.next());
        }
    }

    private void add(int fileCount, File file) throws IOException {
        Tokenizer t = new JavaTokensTokenizer();
        TokenList ts = new TokenList(file.getAbsolutePath());
        FileReader fr = new FileReader(file);
        t.tokenize(ts, fr);
        fr.close();
        tokenSets.add(ts);
    }

    public static void main(String[] args) {
        new Test();
    }
}
