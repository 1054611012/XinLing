package com.xinling.app.domain.model;

import java.util.List;
import java.util.Map;

/**
 * 团队统计VO
 */
public class TeamStatisticsVO {

    private int directCount;
    private int indirectCount;
    private int totalTeamCount;
    private int activeCount;
    private List<Map<String, Object>> directMembers;
    private List<Map<String, Object>> indirectMembers;

    public int getDirectCount() { return directCount; }
    public void setDirectCount(int directCount) { this.directCount = directCount; }

    public int getIndirectCount() { return indirectCount; }
    public void setIndirectCount(int indirectCount) { this.indirectCount = indirectCount; }

    public int getTotalTeamCount() { return totalTeamCount; }
    public void setTotalTeamCount(int totalTeamCount) { this.totalTeamCount = totalTeamCount; }

    public int getActiveCount() { return activeCount; }
    public void setActiveCount(int activeCount) { this.activeCount = activeCount; }

    public List<Map<String, Object>> getDirectMembers() { return directMembers; }
    public void setDirectMembers(List<Map<String, Object>> directMembers) { this.directMembers = directMembers; }

    public List<Map<String, Object>> getIndirectMembers() { return indirectMembers; }
    public void setIndirectMembers(List<Map<String, Object>> indirectMembers) { this.indirectMembers = indirectMembers; }
}
