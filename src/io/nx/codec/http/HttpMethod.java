
package io.nx.codec.http;

import java.util.HashMap;
import java.util.Map;


public class HttpMethod implements Comparable<HttpMethod> {
   
    public static final HttpMethod OPTIONS = new HttpMethod("OPTIONS");

    public static final HttpMethod GET = new HttpMethod("GET");

    public static final HttpMethod HEAD = new HttpMethod("HEAD");

    public static final HttpMethod POST = new HttpMethod("POST");

    public static final HttpMethod PUT = new HttpMethod("PUT");

    public static final HttpMethod PATCH = new HttpMethod("PATCH");

    public static final HttpMethod DELETE = new HttpMethod("DELETE");

    public static final HttpMethod TRACE = new HttpMethod("TRACE");

    public static final HttpMethod CONNECT = new HttpMethod("CONNECT");

    private static final Map<String, HttpMethod> methodMap =
            new HashMap<String, HttpMethod>();

    static {
        methodMap.put(OPTIONS.toString(), OPTIONS);
        methodMap.put(GET.toString(), GET);
        methodMap.put(HEAD.toString(), HEAD);
        methodMap.put(POST.toString(), POST);
        methodMap.put(PUT.toString(), PUT);
        methodMap.put(PATCH.toString(), PATCH);
        methodMap.put(DELETE.toString(), DELETE);
        methodMap.put(TRACE.toString(), TRACE);
        methodMap.put(CONNECT.toString(), CONNECT);
    }

    public static HttpMethod valueOf(String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }

        name = name.trim().toUpperCase();
        if (name.length() == 0) {
            throw new IllegalArgumentException("empty name");
        }

        HttpMethod result = methodMap.get(name);
        if (result != null) {
            return result;
        } else {
            return new HttpMethod(name);
        }
    }

    private final String name;

    public HttpMethod(String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }

        name = name.trim().toUpperCase();
        if (name.length() == 0) {
            throw new IllegalArgumentException("empty name");
        }

        for (int i = 0; i < name.length(); i ++) {
            if (Character.isISOControl(name.charAt(i)) ||
                Character.isWhitespace(name.charAt(i))) {
                throw new IllegalArgumentException("invalid character in name");
            }
        }

        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof HttpMethod)) {
            return false;
        }

        HttpMethod that = (HttpMethod) o;
        return getName().equals(that.getName());
    }

    @Override
    public String toString() {
        return getName();
    }

    public int compareTo(HttpMethod o) {
        return getName().compareTo(o.getName());
    }
}
