package fluentq.sql;

import static fluentq.core.Target.TERADATA;
import static fluentq.sql.Constants.survey;

import fluentq.core.testutil.IncludeIn;
import fluentq.sql.teradata.SetQueryBandClause;
import org.junit.Test;

public abstract class SelectTeradataBase extends AbstractBaseTest {

  protected SetQueryBandClause setQueryBand() {
    return new SetQueryBandClause(connection, configuration);
  }

  @Test
  @IncludeIn(TERADATA)
  public void setQueryBand_forSession() {
    setQueryBand().set("a", "bb").forSession().execute();
    query().from(survey).select(survey.id).fetch();
  }

  @Test
  @IncludeIn(TERADATA)
  public void setQueryBand_forTransaction() {
    setQueryBand().set("a", "bb").forTransaction().execute();
    query().from(survey).select(survey.id).fetch();
  }
}
