package tk.bad_rabbit.rcam.spring.servlet.handlers;

import java.io.File;

import org.glassfish.grizzly.servlet.DefaultServlet;
import org.glassfish.grizzly.utils.ArraySet;

public class DefaultServletImpl extends DefaultServlet {

  protected DefaultServletImpl(ArraySet<File> docRoots) {
    super(docRoots);
    // TODO Auto-generated constructor stub
  }

}
