package net.sourceforge.pmd.util.viewer.util;

import java.util.ResourceBundle;


/**
 * helps with internationalization
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class NLS {
    private final static ResourceBundle bundle;

    static {
        bundle =
                ResourceBundle.getBundle("net.sourceforge.pmd.util.viewer.resources.viewer_strings");
    }

    /**
     * translates the given key to the message
     *
     * @param key key to be translated
     * @return translated string
     */
    public static String nls(String key) {
        return bundle.getString(key);
    }
}


/*
 * $Log$
 * Revision 1.1  2005/08/15 19:51:42  tomcopeland
 * Initial revision
 *
 * Revision 1.2  2004/09/27 19:42:52  tomcopeland
 * A ridiculously large checkin, but it's all just code reformatting.  Nothing to see here...
 *
 * Revision 1.1  2003/09/23 20:32:42  tomcopeland
 * Added Boris Gruschko's new AST/XPath viewer
 *
 * Revision 1.1  2003/09/24 01:33:03  bgr
 * moved to a new package
 *
 * Revision 1.1  2003/09/22 05:21:54  bgr
 * initial commit
 *
 */
