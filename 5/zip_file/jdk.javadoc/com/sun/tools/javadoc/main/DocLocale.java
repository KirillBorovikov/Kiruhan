/*
 * Copyright (c) 2000, 2012, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.sun.tools.javadoc.main;

import java.text.BreakIterator;
import java.text.Collator;
import java.util.Locale;

/**
 * This class holds the information about locales.
 *
 *  <p><b>This is NOT part of any supported API.
 *  If you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 *
 * @since 1.4
 * @author Robert Field
 */
@Deprecated
class DocLocale {

    /**
     * The locale name will be set by Main, if option is provided on the
     * command line.
     */
    final String localeName;

    /**
     * The locale to be used. If user doesn't provide this,
     * then set it to default locale value.
     */
    final Locale locale;

    /**
     * The collator for this application. This is to take care of Locale
     * Specific or Natural Language Text sorting.
     */
    final Collator collator;

    /**
     * Enclosing DocEnv
     */
    private final DocEnv docenv;

    /**
     * Sentence instance from the BreakIterator.
     */
    private final BreakIterator sentenceBreaker;

    /**
     * True is we should use <code>BreakIterator</code>
     * to compute first sentence.
     */
    private boolean useBreakIterator = false;

    /**
     * The HTML sentence terminators.
     */
    static final String[] sentenceTerminators =
                    {
                        "<p>", "</p>", "<h1>", "<h2>",
                        "<h3>", "<h4>", "<h5>", "<h6>",
                        "</h1>", "</h2>", "</h3>", "</h4>", "</h5>",
                        "</h6>", "<hr>", "<pre>", "</pre>"
                    };

    /**
     * Constructor
     */
    DocLocale(DocEnv docenv, String localeName, boolean useBreakIterator) {
        this.docenv = docenv;
        this.localeName = localeName;
        this.useBreakIterator = useBreakIterator;
        locale = getLocale();
        if (locale == null) {
            docenv.exit();
        } else {
            Locale.setDefault(locale); // NOTE: updating global state
        }
        collator = Collator.getInstance(locale);
        sentenceBreaker = BreakIterator.getSentenceInstance(locale);
    }

    /**
     * Get the locale if specified on the command line
     * else return null and if locale option is not used
     * then return default locale.
     */
    private Locale getLocale() {
        Locale userlocale = null;
        if (localeName.length() > 0) {
            int firstuscore = localeName.indexOf('_');
            int seconduscore = -1;
            String language = null;
            String country = null;
            String variant = null;
            if (firstuscore == 2) {
                language = localeName.substring(0, firstuscore);
                seconduscore = localeName.indexOf('_', firstuscore + 1);
                if (seconduscore > 0) {
                    if (seconduscore != firstuscore + 3 ||
                           localeName.length() <= seconduscore + 1) {
                        docenv.error(null, "main.malformed_locale_name", localeName);
                        return null;
                    }
                    country = localeName.substring(firstuscore + 1,
                                                   seconduscore);
                    variant = localeName.substring(seconduscore + 1);
                } else if (localeName.length() == firstuscore + 3) {
                    country = localeName.substring(firstuscore + 1);
                } else {
                    docenv.error(null, "main.malformed_locale_name", localeName);
                    return null;
                }
            } else if (firstuscore == -1 && localeName.length() == 2) {
                language = localeName;
            } else {
                docenv.error(null, "main.malformed_locale_name", localeName);
                return null;
            }
            userlocale = searchLocale(language, country, variant);
            if (userlocale == null) {
                docenv.error(null, "main.illegal_locale_name", localeName);
                return null;
            } else {
                return userlocale;
            }
        } else {
            return Locale.getDefault();
        }
    }

    /**
     * Search the locale for specified language, specified country and
     * specified variant.
     */
    private Locale searchLocale(String language, String country,
                                String variant) {
        for (Locale loc : Locale.getAvailableLocales()) {
            if (loc.getLanguage().equals(language) &&
                (country == null || loc.getCountry().equals(country)) &&
                (variant == null || loc.getVariant().equals(variant))) {
                return loc;
            }
        }
        return null;
    }

    String localeSpecificFirstSentence(DocImpl doc, String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        int index = s.indexOf("-->");
        if(s.trim().startsWith("<!--") && index != -1) {
            return localeSpecificFirstSentence(doc, s.substring(index + 3, s.length()));
        }
        if (useBreakIterator || !locale.getLanguage().equals("en")) {
            sentenceBreaker.setText(s.replace('\n', ' '));
            int start = sentenceBreaker.first();
            int end = sentenceBreaker.next();
            return s.substring(start, end).trim();
        } else {
            return englishLanguageFirstSentence(s).trim();
        }
    }

    /**
     * Return the first sentence of a string, where a sentence ends
     * with a period followed be white space.
     */
    private String englishLanguageFirstSentence(String s) {
        if (s == null) {
            return null;
        }
        int len = s.length();
        boolean period = false;
        for (int i = 0 ; i < len ; i++) {
            switch (s.charAt(i)) {
                case '.':
                    period = true;
                    break;
                case ' ':
                case '\t':
                case '\n':
            case '\r':
            case '\f':
                    if (period) {
                        return s.substring(0, i);
                    }
                    break;
            case '<':
                    if (i > 0) {
                        if (htmlSentenceTerminatorFound(s, i)) {
                            return s.substring(0, i);
                        }
                    }
                    break;
                default:
                    period = false;
            }
        }
        return s;
    }

    /**
     * Find out if there is any HTML tag in the given string. If found
     * return true else return false.
     */
    private boolean htmlSentenceTerminatorFound(String str, int index) {
        for (String terminator : sentenceTerminators) {
            if (str.regionMatches(true, index, terminator,
                                  0, terminator.length())) {
                return true;
            }
        }
        return false;
    }
}
