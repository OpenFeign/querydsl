package com.querydsl.collections;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.set;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import com.querydsl.core.CloseableIterator;
import com.querydsl.core.FetchableQuery;
import com.querydsl.core.ResultTransformer;
import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.types.Projections;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

public class GroupBy3Test {

  @QueryEntity
  public static class RiskAnalysis {
    public String id;
    public Set<AssetThreat> assetThreats;
  }

  @QueryEntity
  public static class AssetThreat {
    public String id;
    public Set<Threat> threats;
  }

  @QueryEntity
  public static class Threat {
    public String id;
  }

  @Test
  public void nested_expressions() {
    var riskAnalysis = QGroupBy3Test_RiskAnalysis.riskAnalysis;
    var assetThreat = QGroupBy3Test_AssetThreat.assetThreat;
    var threat = QGroupBy3Test_Threat.threat;

    ResultTransformer<Map<String, RiskAnalysis>> transformer =
        groupBy(riskAnalysis.id)
            .as(
                Projections.bean(
                    RiskAnalysis.class,
                    riskAnalysis.id,
                    set(Projections.bean(
                            AssetThreat.class,
                            assetThreat.id,
                            set(Projections.bean(Threat.class, threat.id)).as("threats")))
                        .as("assetThreats")));

    var iter = (CloseableIterator) createMock(CloseableIterator.class);
    var projectable = (FetchableQuery) createMock(FetchableQuery.class);
    expect(
            projectable.select(
                Projections.tuple(
                    riskAnalysis.id,
                    riskAnalysis.id,
                    assetThreat.id,
                    Projections.bean(Threat.class, threat.id))))
        .andReturn(projectable);
    expect(projectable.iterate()).andReturn(iter);
    expect(iter.hasNext()).andReturn(false);
    iter.close();
    replay(iter, projectable);

    transformer.transform(projectable);
    verify(projectable);
  }

  @Test
  public void alias_usage() {
    var riskAnalysis = QGroupBy3Test_RiskAnalysis.riskAnalysis;
    var assetThreat = QGroupBy3Test_AssetThreat.assetThreat;
    var threat = QGroupBy3Test_Threat.threat;

    var transformer =
        groupBy(riskAnalysis.id)
            .as(
                riskAnalysis.id,
                set(
                    Projections.bean(
                            AssetThreat.class,
                            assetThreat.id,
                            set(Projections.bean(Threat.class, threat.id)).as("threats"))
                        .as("assetThreats")));

    var iter = (CloseableIterator) createMock(CloseableIterator.class);
    var projectable = (FetchableQuery) createMock(FetchableQuery.class);
    expect(
            projectable.select(
                Projections.tuple(
                    riskAnalysis.id,
                    riskAnalysis.id,
                    assetThreat.id,
                    Projections.bean(Threat.class, threat.id))))
        .andReturn(projectable);
    expect(projectable.iterate()).andReturn(iter);
    expect(iter.hasNext()).andReturn(false);
    iter.close();
    replay(iter, projectable);

    transformer.transform(projectable);
    verify(projectable);
  }
}
