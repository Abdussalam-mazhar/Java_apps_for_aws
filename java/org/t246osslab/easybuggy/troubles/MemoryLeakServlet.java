package org.t246osslab.easybuggy.troubles;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.t246osslab.easybuggy.core.servlets.AbstractServlet;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = { "/memoryleak" })
public class MemoryLeakServlet extends AbstractServlet {

    private HashMap<String, String> cache = new HashMap<String, String>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        StringBuilder bodyHtml = new StringBuilder();
        Locale locale = req.getLocale();
        try {
            toDoRemove();
            
            List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
            for (MemoryPoolMXBean memoryPoolMXBean : memoryPoolMXBeans) {
                if (MemoryType.HEAP.equals(memoryPoolMXBean.getType())) {
                    bodyHtml.append("<p>" + memoryPoolMXBean.getName() + "</p>");
                    bodyHtml.append("<table class=\"table table-striped table-bordered table-hover\" style=\"font-size:small;\">");
                    bodyHtml.append("<tr><th></th>");
                    bodyHtml.append("<th width=\"18%\">" + getMsg("label.memory.init", locale) + "</th>");
                    bodyHtml.append("<th width=\"18%\">" + getMsg("label.memory.used", locale) + "</th>");
                    bodyHtml.append("<th width=\"18%\">" + getMsg("label.memory.committed", locale) + "</th>");
                    bodyHtml.append("<th width=\"18%\">" + getMsg("label.memory.max", locale) + "</th></tr>");
                    writeUsageRow(bodyHtml, memoryPoolMXBean.getUsage(), getMsg("label.memory.usage", locale));
                    writeUsageRow(bodyHtml, memoryPoolMXBean.getPeakUsage(), getMsg("label.memory.peak.usage", locale));
                    writeUsageRow(bodyHtml, memoryPoolMXBean.getCollectionUsage(), getMsg("label.memory.collection.usage", locale));
                    bodyHtml.append("</table>");
                }
            }
            bodyHtml.append(getInfoMsg("msg.note.memoryleak", req.getLocale()));

        } catch (Exception e) {
            log.error("Exception occurs: ", e);
            bodyHtml.append(getErrMsg("msg.unknown.exception.occur", new String[] { e.getMessage() }, locale));
        } finally {
            responseToClient(req, res, getMsg("title.memoryleak.page", locale), bodyHtml.toString());
        }
    }

    private void writeUsageRow(StringBuilder bodyHtml, MemoryUsage usage, String usageName) {
        if (usage != null) {
            bodyHtml.append("<tr><td>" + usageName + "</td>");
            bodyHtml.append("<td>" + usage.getInit() + "</td>");
            bodyHtml.append("<td>" + usage.getUsed() + "</td>");
            bodyHtml.append("<td>" + usage.getCommitted() + "</td>");
            bodyHtml.append("<td>" + (usage.getMax() == -1 ? "[undefined]" : usage.getMax()) + "</td></tr>");
        }
    }

    private void toDoRemove() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100000; i++) {
            sb.append("Memory leak occurs!");
        }
        cache.put(String.valueOf(sb.hashCode()), sb.toString());
    }
}
