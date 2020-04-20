package wlmswingreports;

import java.util.Vector;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

class ReportDataSource implements JRDataSource
{

  Vector<Vector<Object>> data;
  Vector<String> cols;
  int index = -1;

  public ReportDataSource(Vector<String> c, Vector<Vector<Object>> d)
  {
    cols = c;
    data = d;
  }

  @Override
  public boolean next() throws JRException
  {
    index++;
    return index < data.size();
  }

  public int getColIndex(JRField colName)
  {
    String cn = colName.getName();
    int colIndex = -1;
    for (int i = 0; i < cols.size(); i++)
    {
      //System.out.println("cols " + cols.elementAt(i));
      if (cn.equals(cols.elementAt(i)))
      {
        colIndex = i;
      }
    }
    return colIndex;
  }

  @Override
  public Object getFieldValue(JRField colName) throws JRException
  {
    int c = getColIndex(colName);
    if (c != -1 && index < data.size() && c < data.elementAt(index).size())
    {
      Object o = data.elementAt(index).elementAt(c);
      if (o != null)
      {
        try
        {
          if (Class.forName("java.lang.Integer").isInstance(o))
          {
            return Integer.parseInt(o.toString());
          }
          if (Class.forName("java.lang.Float").isInstance(o))
          {
            return Float.parseFloat(o.toString());
          }
          if (Class.forName("java.lang.Double").isInstance(o))
          {
            return Double.parseDouble(o.toString());
          }
          if (Class.forName("java.lang.Boolean").isInstance(o))
          {
            return Boolean.parseBoolean(o.toString());
          }
          else
          {
            return o.toString();
          }
        }
        catch (java.lang.ClassNotFoundException ex)
        {
          ex.printStackTrace();
          return o.toString();
        }
      }
      else
      {
        return null;
      }
    }
    else
    {
      return null;
    }
  }
}
