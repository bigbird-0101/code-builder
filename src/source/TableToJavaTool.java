package source;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.mysql.jdbc.PreparedStatement;


/**
 * 把数据库中的表转化为java对象
 * 
 * @author fpp
 * 
 */
public class TableToJavaTool {
	public static String quDongName;

	// 数据库连接-用户名
	public static String user;

	// 数据库连接-密码
	public static String password;

	// 数据库连接-URL
	public static String dbUrl;

	// 表名
	public static String tableName;

	// 表类别名称
	private String catalog;
	//项目地址
	public static String projectUrl;
	//源代码前缀地址
	public static String beforeSrcUrl;
    //源代码地址
	public static String srcUrl;
    //完整地址
	public static String completeUrl;
	
	public List<Map<String, Object>> getDataList() {
		return dataList;
	}

	public void setDataList(List<Map<String, Object>> dataList) {
		this.dataList = dataList;
	}

	public List<String> getPrimaryKeyList() {
		return primaryKeyList;
	}

	private final List<String> primaryKeyList = new ArrayList<String>();

	private List<Map<String, Object>> dataList = null;

	private Map<String,String> tableCommentMap=new HashMap<String,String>();
	public TableToJavaTool() {

	}

	public void process() {
		List<String> tableNameS = getAllTableName();
		
		for (String tableName : tableNameS) {
			process(tableName);
		}
	}

	public void process(String tableName) {
		getAllTableComment(tableName);//获取所有表注释
		completeUrl=projectUrl+"\\"+beforeSrcUrl+"\\"+srcUrl;
		//校验完整地址是否真实存在
		dataList = readData(tableName);
		//生成bean文件
		createJavaBeanFile(tableName, completeUrl+"\\bean");
		
		//生成controller 文件
	    createJavaControllerFile(tableName, completeUrl+"\\controller");
	    
	     //生成service 文件
	    createJavaServiceFile(tableName, completeUrl+"\\service\\inter");
	    
	    //生成serviceImpl 文件
	    createJavaServiceImplFile(tableName, completeUrl+"\\service\\impl");
	    
	    //生成Dao 文件
	    createJavaDaoFile(tableName, completeUrl+"\\dao");

	}

	private void createJavaDaoFile(String tableName, String daoFileUrl) {
		//如果在目录当中已经有表名一样的目录，那么就放在该目录下面
		String parentFile=getFileInExistFile(daoFileUrl,tableName);
		String parentFileReal=Utils.isEmpty(parentFile)?"":"\\"+parentFile;
		daoFileUrl=daoFileUrl+parentFileReal;
		
		String javaBeanClassName=getJavaClassName(tableName);
		String javaDaoName=javaBeanClassName+"Dao";

		
		String lowerCaseBeanName=firstLowerCase(javaBeanClassName);
		
		//获取表注释  如果表注释最后一个字有表字那么去除这个字
		String tableComment=tableCommentMap.get(tableName);
		if(!Utils.isEmpty(tableComment)&&"表".equals(tableComment.substring(tableComment.length()-1))){
			tableComment=tableComment.substring(0,tableComment.length()-1);
		}
		
		// 获取文件下的定义的包名
		StringBuffer jbString = new StringBuffer();
		jbString.append("package ").append(getPackName(daoFileUrl) + ";").append("\r\n\n");
		
		//导入jar包
		//bean 包
		String beanImportJar="import "+srcUrl.replaceAll("\\\\",".")+".bean."+(Utils.isEmpty(parentFile)?"":parentFile+".")+""+javaBeanClassName+";\r\n";
		jbString.append(beanImportJar)
	            .append("import java.util.List;\r\n" + 
	            		"\r\n" + 
	            		"import org.apache.ibatis.annotations.Delete;\r\n" + 
	            		"import org.apache.ibatis.annotations.Insert;\r\n" + 
	            		"import org.apache.ibatis.annotations.Mapper;\r\n" + 
	            		"import org.apache.ibatis.annotations.One;\r\n" + 
	            		"import org.apache.ibatis.annotations.Param;\r\n" + 
	            		"import org.apache.ibatis.annotations.Result;\r\n" + 
	            		"import org.apache.ibatis.annotations.Results;\r\n" + 
	            		"import org.apache.ibatis.annotations.Select;\r\n" + 
	            		"import org.apache.ibatis.annotations.Update;\r\n" + 
	            		"import org.apache.ibatis.mapping.FetchType;\r\n")
		        .append("import java.util.List;\r\n");
		
		//拼接类的定义
		jbString.append("/**\r\n" + 
						" * "+tableComment+"业务处理接口DB类 \r\n" + 
						" * @author fpp\r\n" + 
						" */\r\n")
		        .append("@Mapper\r\n")
		        .append("public interface ").append(javaDaoName).append("\r\n")
		        .append("{").append("\r\n");
		jbString.append("	//添加"+tableComment+"\r\n" + 
		                "   @Insert({\r\n"+
				        "   "+getAddSql(tableName)+""+
				        "   })\r\n"+
				        "	int add"+javaBeanClassName+"("+javaBeanClassName+" "+lowerCaseBeanName+");\r\n")
		        .append("	//删除"+tableComment+" 根据其ID数组\r\n" +
		                "   @Delete({\r\n"+
				        "   "+getDeleteSql(tableName)+""+
				        "   })\r\n"+
		        		"	int delete"+javaBeanClassName+"(List<String> idArray);\r\n")
		        .append("	//编辑"+tableComment+"\r\n" +
		                "   @Update({\r\n"+
				        "   "+getEditSql(tableName)+""+
				        "   })\r\n"+
		        		"	int edit"+javaBeanClassName+"("+javaBeanClassName+" "+lowerCaseBeanName+");\r\n")
		        .append("	//根据ID获取"+tableComment+"信息\r\n" +
		                "   @Select({\r\n"+
				        "   "+getSelectByIdSql(tableName)+""+
				        "   })\r\n"+
		        		"	"+javaBeanClassName+" get"+javaBeanClassName+"ById(@Param(\"id\") String id);\r\n")
		        .append("	//根据所属者id和关键字获取"+tableComment+"信息个数\r\n" +
		                "   @Select({\r\n"+
				        "   "+getSelectCountSql(tableName)+""+
				        "   })\r\n"+
		        		"	int get"+javaBeanClassName+"Count(String belongToId,String findKey);\r\n")
		        .append("	//分页方法获取"+tableComment+"信息列表\r\n" +
		                "   @Select({\r\n"+
				        "   "+getSelectListSql(tableName)+""+
				        "   })\r\n"+
		        		"	List<"+javaBeanClassName+"> getAll"+javaBeanClassName+"List(String belongToId,String findKey,Integer offset,Integer pageCount);\r\n");
		jbString.append("}\r\n");
		jbString.append("//");
		
		buildTargetFile(jbString.toString(),daoFileUrl+"\\"+javaDaoName);
		
	}

	private void createJavaServiceImplFile(String tableName, String serviceImplFileUrl) {
		//如果在目录当中已经有表名一样的目录，那么就放在该目录下面
		String parentFile=getFileInExistFile(serviceImplFileUrl,tableName);
		String parentFileReal=Utils.isEmpty(parentFile)?"":"\\"+parentFile;
		serviceImplFileUrl=serviceImplFileUrl+parentFileReal;
		
		String javaBeanClassName=getJavaClassName(tableName);
		String javaServiceName=javaBeanClassName+"Service";
		String javaServiceImplName=javaBeanClassName+"ServiceImpl";
		String javaDaoName=javaBeanClassName+"Dao";

		String lowerDaoCaseName=firstLowerCase(javaDaoName);
        String lowerBeanName=firstLowerCase(javaBeanClassName);
		//获取表注释  如果表注释最后一个字有表字那么去除这个字
		String tableComment=tableCommentMap.get(tableName);
		if(!Utils.isEmpty(tableComment)&&"表".equals(tableComment.substring(tableComment.length()-1))){
			tableComment=tableComment.substring(0,tableComment.length()-1);
		}
		
		// 获取文件下的定义的包名
		StringBuffer jbString = new StringBuffer();
		jbString.append("package ").append(getPackName(serviceImplFileUrl) + ";").append("\r\n\n");
		
		//导入jar包
		//bean 包
		String beanImportJar="import "+srcUrl.replaceAll("\\\\",".")+".bean."+javaBeanClassName+";\r\n";
		String daoImportJar="import "+srcUrl.replaceAll("\\\\",".")+".dao."+javaDaoName+";\r\n";
		jbString.append(beanImportJar)
		        .append(daoImportJar)
		        .append("import "+srcUrl.replaceAll("\\\\",".")+".service.inter."+javaServiceName+";\r\n")
		        .append("import java.util.Arrays;\r\n")
		        .append("import java.util.Objects;\r\n")
		        .append("import "+srcUrl.replaceAll("\\\\",".")+".comm.util.CoreUtil;\r\n")
		        .append("import java.util.stream.Collectors;\r\n")
		        .append("import org.apache.logging.log4j.util.Strings;\r\n")
		        .append("import org.springframework.beans.factory.annotation.Autowired;\r\n" + 
		        		"import org.springframework.stereotype.Service;\r\n" + 
		        		"import org.springframework.transaction.annotation.Transactional;\r\n")
		        .append("import java.util.List;\r\n");
		
		//拼接类的定义
		jbString.append("/**\r\n" + 
						" * "+tableComment+"业务处理接口实现类 \r\n" + 
						" * @author fpp\r\n" + 
						" */\r\n")
		        .append("@Service\r\n" + 
		        		"@Transactional\r\n")
		        .append("public class ").append(javaServiceImplName+" implements "+javaServiceName+" ").append("\r\n")
		        .append("{").append("\r\n");
		//拼接全局变量
		jbString.append("   @Autowired\r\n")
		        .append("   private "+javaDaoName+" "+lowerDaoCaseName+";");
		        
		jbString.append("	//添加"+tableComment+"\r\n" + 
		                "   @Override\r\n"+
				        "	public boolean add"+javaBeanClassName+"("+javaBeanClassName+" "+lowerBeanName+"){\r\n")
		        .append("       Objects.requireNonNull("+lowerBeanName+");\r\n" + 
		        		"		CoreUtil.setBeanInsertDateTime("+lowerBeanName+", "+lowerBeanName+".getOperatorId());\r\n" + 
		        		"		return "+lowerDaoCaseName+".add"+javaBeanClassName+"("+lowerBeanName+")==1;\r\n")
		        .append("	}\r\n")
		        .append("	//删除"+tableComment+" 根据其ID数组\r\n" +
		                "   @Override\r\n"+
		        		"	public boolean delete"+javaBeanClassName+"(String idS){\r\n")
		        .append("        if(Strings.isNotBlank(idS)) {\r\n" + 
		        		"			List<String> idArray=Arrays.stream(idS.split(\",\")).filter(i->Strings.isNotBlank(i)).distinct().collect(Collectors.toList());\r\n" + 
		        		"			return "+lowerDaoCaseName+".delete"+javaBeanClassName+"(idArray)==idArray.size();\r\n" + 
		        		"		}\r\n" + 
		        		"		return false;\r\n")
		        .append("	}\r\n")
		        .append("	//编辑"+tableComment+"\r\n" +
		                "   @Override\r\n"+
		        		"	public boolean edit"+javaBeanClassName+"("+javaBeanClassName+" "+lowerBeanName+"){\r\n")
		        .append("        Objects.requireNonNull("+lowerBeanName+");\r\n" + 
		        		"		CoreUtil.setBeanUpdateDateTime("+lowerBeanName+", "+lowerBeanName+".getOperatorId());\r\n" + 
		        		"		return "+lowerDaoCaseName+".edit"+javaBeanClassName+"("+lowerBeanName+")==1;\r\n")
		        .append("	}\r\n")
		        .append("	//根据ID获取"+tableComment+"信息\r\n" +
		                "   @Override\r\n"+
		        		"	public "+javaBeanClassName+" get"+javaBeanClassName+"ById(String id){\r\n")
		        .append("        if(!Strings.isBlank(id)) {\r\n" + 
		        		"			"+javaBeanClassName+" "+lowerDaoCaseName+"="+lowerDaoCaseName+".get"+javaBeanClassName+"ById(id);\r\n" + 
		        		"			return "+lowerDaoCaseName+";\r\n" + 
		        		"		}\r\n"+
		        		"       return null;\r\n")
		        .append("	}\r\n")
		        .append("	//根据所属者id和关键字获取"+tableComment+"信息个数\r\n" +
		        		"   @Override\r\n"+
		        		"	public int get"+javaBeanClassName+"Count(String belongToId,String findKey){\r\n")
		        .append("       return "+lowerDaoCaseName+".get"+javaBeanClassName+"Count(belongToId,findKey);\r\n")
		        .append("	}\r\n")
		        .append("	//分页方法获取"+tableComment+"信息列表\r\n" +
		                "   @Override\r\n"+
		        		"	public List<"+javaBeanClassName+"> getAll"+javaBeanClassName+"List(String belongToId,String findKey,Integer offset,Integer pageCount){\r\n")
		        .append("       return "+lowerDaoCaseName+".getAll"+javaBeanClassName+"List(belongToId,findKey,offset,pageCount);\r\n")
		        .append("	}\r\n");
		jbString.append("}\r\n");
		jbString.append("//");
		
		buildTargetFile(jbString.toString(),serviceImplFileUrl+"\\"+javaServiceImplName);
	}

	private void createJavaServiceFile(String tableName, String serviceFileUrl) {
		//如果在目录当中已经有表名一样的目录，那么就放在该目录下面
		String parentFile=getFileInExistFile(serviceFileUrl,tableName);
		String parentFileReal=Utils.isEmpty(parentFile)?"":"\\"+parentFile;
		serviceFileUrl=serviceFileUrl+parentFileReal;
		
		String javaBeanClassName=getJavaClassName(tableName);
		String javaServiceName=javaBeanClassName+"Service";
		
		String lowerCaseBeanName=firstLowerCase(javaBeanClassName);
		
		//获取表注释  如果表注释最后一个字有表字那么去除这个字
		String tableComment=tableCommentMap.get(tableName);
		if(!Utils.isEmpty(tableComment)&&"表".equals(tableComment.substring(tableComment.length()-1))){
			tableComment=tableComment.substring(0,tableComment.length()-1);
		}
		
		// 获取文件下的定义的包名
		StringBuffer jbString = new StringBuffer();
		jbString.append("package ").append(getPackName(serviceFileUrl) + ";").append("\r\n\n");
		
		//导入jar包
		//bean 包
		String beanImportJar="import "+srcUrl.replaceAll("\\\\",".")+".bean."+(Utils.isEmpty(parentFile)?"":parentFile+".")+""+javaBeanClassName+";\r\n";
		jbString.append(beanImportJar)
		        .append("import java.util.List;\r\n");
		
		//拼接类的定义
		jbString.append("/**\r\n" + 
						" * "+tableComment+"业务处理接口类 \r\n" + 
						" * @author fpp\r\n" + 
						" */\r\n")
		        .append("public interface ").append(javaServiceName)
		        .append("{").append("\r\n");
		jbString.append("	//添加"+tableComment+"\r\n" + 
				        "	boolean add"+javaBeanClassName+"("+javaBeanClassName+" "+lowerCaseBeanName+");\r\n")
		        .append("	//删除"+tableComment+" 根据其ID数组\r\n" + 
		        		"	boolean delete"+javaBeanClassName+"(String idS);\r\n")
		        .append("	//编辑"+tableComment+"\r\n" + 
		        		"	boolean edit"+javaBeanClassName+"("+javaBeanClassName+" "+lowerCaseBeanName+");\r\n")
		        .append("	//根据ID获取"+tableComment+"信息\r\n" + 
		        		"	"+javaBeanClassName+" get"+javaBeanClassName+"ById(String id);\r\n")
		        .append("	//根据所属者id和关键字获取"+tableComment+"信息个数\r\n" + 
		        		"	int get"+javaBeanClassName+"Count(String belongToId,String findKey);\r\n")
		        .append("	//分页方法获取"+tableComment+"信息列表\r\n" + 
		        		"	List<"+javaBeanClassName+"> getAll"+javaBeanClassName+"List(String belongToId,String findKey,Integer offset,Integer pageCount);\r\n");
		jbString.append("}\r\n");
		jbString.append("//");
		
		buildTargetFile(jbString.toString(),serviceFileUrl+"\\"+javaServiceName);
	}

	private void createJavaControllerFile(String tableName, String controllerFileUrl) {
		//如果在目录当中已经有表名一样的目录，那么就放在该目录下面
		String parentFile=getFileInExistFile(controllerFileUrl,tableName);
		String parentFileReal=Utils.isEmpty(parentFile)?"":"\\"+parentFile;
		controllerFileUrl=controllerFileUrl+parentFileReal;
		
		String javaBeanClassName=getJavaClassName(tableName);
		String javaClassControllerName=javaBeanClassName+"Controller";
		String javaServiceName=javaBeanClassName+"Service";
		
		String lowerCaseBeanName=firstLowerCase(javaBeanClassName);
		// 获取文件下的定义的包名
		StringBuffer jbString = new StringBuffer();
		jbString.append("package ").append(getPackName(controllerFileUrl) + ";").append("\r\n\n");
		
		//获取表注释  如果表注释最后一个字有表字那么去除这个字
		String tableComment=tableCommentMap.get(tableName);
		if(!Utils.isEmpty(tableComment)&&"表".equals(tableComment.substring(tableComment.length()-1))){
			tableComment=tableComment.substring(0,tableComment.length()-1);
		}
		//导入jar包
		//bean 包
		String beanImportJar="import "+srcUrl.replaceAll("\\\\",".")+".bean."+(Utils.isEmpty(parentFile)?"":parentFile+".")+""+javaBeanClassName+";\r\n";
		//service包
		String serviceImportJar="import "+srcUrl.replaceAll("\\\\",".")+".service.inter."+javaServiceName+";\r\n";

		jbString.append("import org.springframework.beans.factory.annotation.Autowired;\r\n" + 
						"import org.springframework.stereotype.Controller;\r\n" + 
						"import org.springframework.validation.BindingResult;\r\n" + 
						"import org.springframework.validation.ObjectError;\r\n" + 
						"import org.springframework.web.bind.annotation.RestController;\r\n" + 
						"import org.springframework.web.bind.annotation.RequestMethod;\r\n" + 
						"import org.springframework.web.bind.annotation.RequestParam;\r\n" + 
						"import org.springframework.web.bind.annotation.ResponseBody;\r\n"+
						"import org.springframework.validation.annotation.Validated;\r\n" + 
						"import org.springframework.web.bind.annotation.RequestMapping;\r\n" + 
						"import org.springframework.web.bind.annotation.RequestBody;\r\n")
		        .append(beanImportJar+"\r\n")
		        .append(serviceImportJar+"\r\n")
                .append("import com.alibaba.fastjson.JSON;\r\n" + 
                		"import com.zzd.ptt.bean.ReturnValue;\r\n")
                .append("import java.util.List;\r\n" + 
                		"import java.util.Objects;\r\n")
                .append("import io.swagger.annotations.Api;\r\n" + 
                		"import io.swagger.annotations.ApiImplicitParam;\r\n" + 
                		"import io.swagger.annotations.ApiImplicitParams;\r\n" + 
                		"import io.swagger.annotations.ApiOperation;\r\n");
		
		//拼接类的定义
		jbString.append("/**\r\n" + 
						" * "+tableComment+"类 \r\n" + 
						" * @author fpp\r\n" + 
						" */\r\n")
		        .append("@RestController\r\n" + 
				"@RequestMapping(\"/controller/"+javaBeanClassName.toLowerCase()+"/"+javaClassControllerName.toLowerCase()+"/\")\r\n")
	            .append("@Api(value = \""+javaClassControllerName+"|"+tableComment+"控制器\")\r\n")
		        .append("public class ").append(javaClassControllerName).append("\r\n")
		        .append("{").append("\r\n");
		
		//拼接全局变量
		jbString.append("    //业务处理类\r\n")
		        .append("    @Autowired\r\n")
		        .append("    private "+javaServiceName+" "+firstLowerCase(javaServiceName)+";\r\n\n");
		
		//拼接增加 删除 、 修改、根据id获取bean,查找所有的记录(包含分页)
		
		//增加
		jbString.append("    /**\r\n" + 
						"    * 添加"+tableComment+"\r\n" + 
						"    * @param "+firstLowerCase(javaBeanClassName)+" "+tableComment+"信息 \r\n" +
						"    * @param errorResult 错误结果信息\r\n" + 
						"    * @return\r\n" + 
						"    */\r\n")
		        .append("    @RequestMapping(value =\"/add"+javaBeanClassName+"\", method= RequestMethod.POST)\r\n")
				.append("    @ApiOperation(value=\"添加"+tableComment+"\", notes=\"\")\r\n")
				.append("    public ReturnValue add"+javaBeanClassName+"(@Validated @RequestBody "+javaBeanClassName+" "+firstLowerCase(javaBeanClassName)+",BindingResult errorResult) {\r\n" + 
						"		try {\r\n" + 
						"			List<ObjectError> errorList=errorResult.getAllErrors();\r\n" + 
						"			if(!Objects.isNull(errorList)&&!errorList.isEmpty()) {\r\n" + 
						"				String message=errorList.stream().findFirst().get().getDefaultMessage();\r\n" + 
						"				return new ReturnValue(false,message);\r\n" + 
						"			}\r\n" + 
						"			boolean flag="+lowerCaseBeanName+"Service.add"+javaBeanClassName+"("+lowerCaseBeanName+");\r\n" + 
						"		    return new ReturnValue(flag);\r\n" + 
						"		}catch(Exception e) {\r\n" + 
						"			e.printStackTrace();\r\n" + 
						"			return new ReturnValue(false,e.getMessage());\r\n" + 
						"		}\r\n" + 
						"	}\r\n");
		
		//删除
		jbString.append("    /**\r\n" + 
						"    * 删除"+tableComment+"根据ID数组\r\n" + 
						"    * @param idS "+tableComment+"ID数组 \r\n" +
						"    * @return\r\n" + 
						"    */\r\n")
		        .append("    @RequestMapping(value =\"/delete"+javaBeanClassName+"\", method= RequestMethod.DELETE)\r\n")
				.append("    @ApiOperation(value=\"删除"+tableComment+"\", notes=\"\")\r\n")
				.append("    @ApiImplicitParam(paramType=\"query\", name = \"idS\", value = \""+tableComment+"ID数组\", required = true, dataType = \"String\")\r\n")
				.append("    public ReturnValue delete"+javaBeanClassName+"(@RequestParam String idS) {\r\n" + 
						"		try {\r\n"+
						"			boolean flag="+lowerCaseBeanName+"Service.delete"+javaBeanClassName+"(idS);\r\n" + 
						"		    return new ReturnValue(flag);\r\n" + 
						"		}catch(Exception e) {\r\n" + 
						"			e.printStackTrace();\r\n" + 
						"			return new ReturnValue(false,e.getMessage());\r\n" + 
						"		}\r\n" + 
						"	}\r\n");
		
		//修改
		jbString.append("    /**\r\n" + 
						"    * 修改"+tableComment+"\r\n" + 
						"    * @param "+firstLowerCase(javaBeanClassName)+" "+tableComment+"信息 \r\n" +
						"    * @param errorResult 错误结果信息\r\n" + 
						"    * @return\r\n" + 
						"    */\r\n")
		        .append("    @RequestMapping(value =\"/edit"+javaBeanClassName+"\", method= RequestMethod.POST)\r\n")
				.append("    @ApiOperation(value=\"修改"+tableComment+"\", notes=\"\")\r\n")
				.append("    public ReturnValue edit"+javaBeanClassName+"(@Validated @RequestBody "+javaBeanClassName+" "+firstLowerCase(javaBeanClassName)+",BindingResult errorResult) {\r\n" + 
						"		try {\r\n" + 
						"			List<ObjectError> errorList=errorResult.getAllErrors();\r\n" + 
						"			if(!Objects.isNull(errorList)&&!errorList.isEmpty()) {\r\n" + 
						"				String message=errorList.stream().findFirst().get().getDefaultMessage();\r\n" + 
						"				return new ReturnValue(false,message);\r\n" + 
						"			}\r\n" + 
						"			boolean flag="+lowerCaseBeanName+"Service.edit"+javaBeanClassName+"("+lowerCaseBeanName+");\r\n" + 
						"		    return new ReturnValue(flag);\r\n" + 
						"		}catch(Exception e) {\r\n" + 
						"			e.printStackTrace();\r\n" + 
						"			return new ReturnValue(false,e.getMessage());\r\n" + 
						"		}\r\n" + 
						"	}\r\n");
		
		
		//根据ID获取bean
		jbString.append("    /**\r\n" + 
						"    * 根据ID 获取"+tableComment+"\r\n" + 
						"    * @param id "+tableComment+"的唯一ID\r\n" +
						"    * @return\r\n" + 
						"    */\r\n")
		        .append("    @RequestMapping(value =\"/get"+javaBeanClassName+"ById\", method= RequestMethod.POST)\r\n")
				.append("    @ApiOperation(value=\"根据id获取"+tableComment+"\", notes=\"返回"+tableComment+"详细信息\")\r\n")
				.append("    @ApiImplicitParam(paramType=\"query\", name = \"id\", value = \""+tableComment+"ID\", required = true, dataType = \"String\")\r\n")
				.append("    public ReturnValue get"+javaBeanClassName+"ById(@RequestParam String id) {\r\n" + 
						"		try {\r\n"+
						"			"+javaBeanClassName+" "+lowerCaseBeanName+"="+lowerCaseBeanName+"Service.get"+javaBeanClassName+"ById(id);\r\n" + 
						"		    return new ReturnValue(true,\"\",(JSON)JSON.toJSON("+lowerCaseBeanName+"));\r\n" + 
						"		}catch(Exception e) {\r\n" + 
						"			e.printStackTrace();\r\n" + 
						"			return new ReturnValue(false,e.getMessage());\r\n" + 
						"		}\r\n" + 
						"	}\r\n");
		
		//分页查询方法
		jbString.append("    /**\r\n" + 
		                "    * 分页方法\r\n"+
						"    * 获取"+tableComment+"列表信息\r\n" + 
						"    * @param id "+tableComment+"所属者的ID\r\n" +
						"    * @param findKey "+tableComment+" 信息关键字\r\n" +
						"    * @param indexofPage 当前页数\r\n" +
						"    * @param pageCount 当前页总数\r\n" +
						"    * @return\r\n" + 
						"    */\r\n")
		        .append("    @RequestMapping(value =\"/getAll"+javaBeanClassName+"List\", method= RequestMethod.GET)\r\n")
				.append("    @ApiOperation(value=\"获取"+tableComment+"列表\", notes=\"返回"+tableComment+"列表信息\")\r\n")
				.append("    @ApiImplicitParams({\r\n" + 
						"        @ApiImplicitParam(paramType=\"query\", name = \"id\", value = \"所属者的id\", required = true, dataType = \"String\"),\r\n" +
						"        @ApiImplicitParam(paramType=\"query\", name = \"findKey\", value = \""+tableComment+"信息关键字\", required = true, dataType = \"String\"),\r\n" + 
						"        @ApiImplicitParam(paramType=\"query\", name = \"indexofPage\", value = \"当前页数,如果不传默认为1\", required = false, dataType = \"Integer\"),\r\n" + 
						"        @ApiImplicitParam(paramType=\"query\", name = \"pageCount\", value = \"每页记录数，如果不传默认为20\", required = true, dataType = \"Integer\"),\r\n" + 
						"    })\r\n")
				.append("    public ReturnValue getAll"+javaBeanClassName+"List(@RequestParam String belongToId,@RequestParam String findKey,@RequestParam Integer indexofPage,@RequestParam Integer pageCount) {\r\n" + 
						"		try {\r\n" + 
						"			//获取当前记录的总数\r\n" + 
						"			if(null==indexofPage) {\r\n" + 
						"    			indexofPage=1;\r\n" + 
						"    		}\r\n" + 
						"    		if(null==pageCount) {\r\n" + 
						"    			pageCount=20;\r\n" + 
						"    		}\r\n" + 
						"    		//根据页码得到当前需要显示的记录数\r\n" + 
						"    		int offset=(indexofPage-1)*pageCount;\r\n" + 
						"    		int "+lowerCaseBeanName+"Count="+lowerCaseBeanName+"Service.get"+javaBeanClassName+"Count(belongToId,findKey);\r\n" + 
						"			List<"+javaBeanClassName+"> result="+lowerCaseBeanName+"Service.getAll"+javaBeanClassName+"List(belongToId,findKey,offset,pageCount);\r\n" + 
						"		    return CoreUtil.returnValue((JSON)JSON.toJSON(result),"+lowerCaseBeanName+"Count,indexofPage,pageCount);\r\n" + 
						"		}catch(Exception e) {\r\n" + 
						"			e.printStackTrace();\r\n" + 
						"			return new ReturnValue(false,e.getMessage());\r\n" + 
						"		}\r\n"+
						"	}\r\n");
		
		jbString.append("}\r\n");
		jbString.append("//");
		
		buildTargetFile(jbString.toString(),controllerFileUrl+"\\"+javaClassControllerName);

	}

	
	public void createJavaBeanFile(String tableName, String fileUrl) {
		//如果在目录当中已经有表名一样的目录，那么就放在该目录下面
		String parentFile=getFileInExistFile(fileUrl,tableName);
		String parentFileReal=Utils.isEmpty(parentFile)?"":"\\"+parentFile;
		fileUrl=fileUrl+parentFileReal;
		
		//获取表注释  如果表注释最后一个字有表字那么去除这个字
		String tableComment=tableCommentMap.get(tableName);
		if(!Utils.isEmpty(tableComment)&&"表".equals(tableComment.substring(tableComment.length()-1))){
			tableComment=tableComment.substring(0,tableComment.length()-1);
		}
		
		String javaClassName=getJavaClassName(tableName);
		// 获取文件下的定义的包名
		StringBuffer jbString = new StringBuffer();
		jbString.append("package ").append(getPackName(fileUrl) + ";").append("\r\n\n");
		
		// 导入jar包
		if (dataList != null) {
			boolean flag = true;
			for (Map<String, Object> map : dataList) {
				String javaType = getJavaType((Integer) map.get("dataType"));
				if ("Date".equals(javaType)) {
					if (jbString.toString().indexOf("import java.util.Date;") < 0) {
						flag = false;
						jbString.append("import java.util.Date;").append("\r\n");
					}
				}

			}
			jbString.append("import io.swagger.annotations.ApiModel;\r\n" + 
					"import io.swagger.annotations.ApiModelProperty;\r\n");
			if (!flag) {
				jbString.append("\n");
			}
		}
		jbString.append("@ApiModel(value=\""+tableComment+"对象模型\")\r\n");
		jbString.append("public class ").append(javaClassName).append(" implements CoreBean\r\n");
		jbString.append("{").append("\r\n");

		if (dataList != null) {
			for (Map<String, Object> map : dataList) {
				String fieldName = getFieldName((String) map.get("columnName"));
				String javaType = getJavaType((Integer) map.get("dataType"));
				jbString.append("    // ").append(map.get("remarks")).append("\r\n");
				jbString.append("    @ApiModelProperty(value=\""+map.get("remarks")+"\")\r\n");
				jbString.append("    private ").append(javaType).append(" ").append(fieldName).append(";")
						.append("\r\n");
				jbString.append("\r\n");
			}

			for (Map<String, Object> map : dataList) {
				String fieldName = getFieldName((String) map.get("columnName"));
				String javaType = getJavaType((Integer) map.get("dataType"));
				jbString.append("    public ").append(javaType).append(" get").append(firstUpperCase(fieldName))
						.append("()").append("\r\n");
				jbString.append("    {").append("\r\n");
				jbString.append("        return ").append(fieldName).append(";").append("\r\n");
				jbString.append("    }").append("\r\n");

				jbString.append("\r\n");

				jbString.append("    public void set").append(firstUpperCase(fieldName)).append("(").append(javaType)
						.append(" ").append(fieldName).append(")").append("\r\n");
				jbString.append("    {").append("\r\n");
				jbString.append("        this.").append(fieldName).append(" = ").append(fieldName).append(";")
						.append("\r\n");
				jbString.append("    }").append("\r\n");

				jbString.append("\r\n");

			}
//			//拼接toString 方法
//			jbString.append("    public ").append("String").append(" toString")
//			.append("()").append("\r\n");
//	        jbString.append("    {").append("\r\n");
//			jbString.append("        return \""+javaClassName+"[\\r\\n\"+\r\n");
//			for (Map<String, Object> map : dataList) {
//				String fieldName = getFieldName((String) map.get("columnName"));
//				jbString.append("        ").append("\""+fieldName+"====="+"\"+"+fieldName+"+").append("\"\\r\\n\"+\r\n");
//			}
//			jbString.append("        \"]\";\r\n");
//			jbString.append("    }").append("\r\n");

			jbString.append("\r\n");
		}

		jbString.append("}\r\n");
		jbString.append("//");
		
		buildTargetFile(jbString.toString(),fileUrl+"\\"+javaClassName);
	}
	
	
	public void buildTargetFile(String fileStr,String fileUrl){
		// 创建文件
		File a = new File(fileUrl + ".java");
		if(a.exists()) {
			a= new File(fileUrl + "_1.java");
		}
		Utils.mkDirs(a);
		try {
			FileOutputStream fops = new FileOutputStream(a);
			fops.write(fileStr.getBytes("utf-8"));
			fops.flush();
			fops.close();
		} catch (FileNotFoundException e) {
//			Utils.mkDirs(a);
//			try {
//				FileOutputStream fops = new FileOutputStream(a);
//				fops.write(fileStr.getBytes());
//				fops.flush();
//				fops.close();
//			}catch(Exception ex) {
//				
//			}
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取数据库连接
	 * 
	 * @return Connection 数据库连接对象
	 */
	private static Connection getConnection() {
		Connection conn = null;
		try {
			Properties props = new Properties();
			props.put("remarksReporting", "true");
			props.put("user", user);
			props.put("password", password);

			Class.forName(quDongName);
			conn = DriverManager.getConnection(dbUrl, props);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return conn;
	}

	/**
	 * 获取数据库指定表的列信息
	 * 
	 * @param tableName
	 *            表名
	 * @return List<Map<String, Object>> 列信息列表
	 */
	private List<Map<String, Object>> readData(String tableName) {
		Connection conn = getConnection();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

		try {
			DatabaseMetaData dbmd = conn.getMetaData();

			ResultSet rs = dbmd.getColumns(getCatalog(), null, tableName, null);
			Map<String, Object> map = null;
			while (rs.next()) {
				map = new HashMap<String, Object>();
				map.put("columnName", rs.getString("COLUMN_NAME"));
				map.put("dataType", rs.getInt("DATA_TYPE"));
				map.put("remarks", rs.getString("REMARKS"));
				dataList.add(map);
			}

			ResultSet rs1 = dbmd.getPrimaryKeys(getCatalog(), null, tableName);
			while (rs1.next()) {
				primaryKeyList.add(rs1.getString("COLUMN_NAME"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return dataList;
	}

	public static String getPackName(String fileUrl) {
		String result = "";
		if (!Utils.isEmpty(fileUrl)) {
			int index = fileUrl.indexOf("java");
			result = fileUrl.substring(index + 4, fileUrl.length());
			result = result.replaceAll("\\\\", ".");
			result = result.substring(1, result.length());
		}
		return result;
	}

	
	private String getJavaClassName(String tableName) {
		String[] valueClassNameS=tableName.substring(4).split("_");
		return Arrays.stream(valueClassNameS).map(s->firstUpperCase(s)).reduce((s,b)->s+b).get();
	}

	public static String getFileInExistFile(String fileUrl,String tableName) {
		File rootFile=new File(fileUrl);
		if(rootFile.exists()) {
			return Arrays.stream(rootFile.list()).filter(s->s.indexOf(tableName.toLowerCase())>-1).distinct().findAny().orElse(null);
		}
		return null;
	}

	/**
	 * 把以_分隔的列明转化为字段名
	 * 
	 * @param columnName
	 *            列名
	 * @return String 字段名
	 */
	private static String getFieldName(String columnName) {
		if (columnName == null) {
			return "";
		}

		StringBuffer fieldNameBuffer = new StringBuffer();

		boolean nextUpperCase = false;
		for (int i = 0; i < columnName.length(); i++) {
			char c = columnName.charAt(i);

			if (nextUpperCase) {
				fieldNameBuffer.append(columnName.substring(i, i + 1).toUpperCase());
			} else {
				fieldNameBuffer.append(c);
			}

			if (c == '_') {
				nextUpperCase = true;
			} else {
				nextUpperCase = false;
			}
		}

		String fieldName = fieldNameBuffer.toString();
		fieldName = fieldName.replaceAll("_", "");
		return fieldName;
	}

	/**
	 * 字符串的第一个字母大写
	 * 
	 * @param str
	 *            字符串
	 * @return String 处理后的字符串
	 */
	private static String firstUpperCase(String str) {
		if (str == null) {
			return "";
		}

		if (str.length() == 1) {
			str = str.toUpperCase();
		} else {
			str = str.substring(0, 1).toUpperCase() + str.substring(1);
		}
		return str;
	}
	
	/**
	 * 字符串的第一个字母小写
	 * 
	 * @param str
	 *            字符串
	 * @return String 处理后的字符串
	 */
	private static String firstLowerCase(String str) {
		if (str == null) {
			return "";
		}

		if (str.length() == 1) {
			str = str.toLowerCase();
		} else {
			str = str.substring(0, 1).toLowerCase() + str.substring(1);
		}
		return str;
	}


	/**
	 * 将数据库列类型转换为java数据类型
	 * 
	 * @param dataType
	 *            列类型
	 * @return String java数据类型
	 */
	private static String getJavaType(int dataType) {
		String javaType = "";
		if (dataType == Types.INTEGER) {
			javaType = "Integer";
		} else if (dataType == Types.BIGINT) {
			javaType = "Long";
		} else if (dataType == Types.CHAR || dataType == Types.VARCHAR || dataType == Types.NVARCHAR
				|| dataType == Types.CLOB||dataType == Types.BLOB) {
			javaType = "String";
		} else if (dataType == Types.TINYINT) {
			javaType = "Short";
		} else if (dataType == Types.FLOAT) {
			javaType = "float";
		} else if (dataType == Types.NUMERIC || dataType == Types.DECIMAL || dataType == Types.DOUBLE) {
			javaType = "BigDecimal";
		} else if (dataType == Types.DATE || dataType == Types.TIMESTAMP||dataType == Types.TIME) {
			javaType = "String";
		}
		return javaType;
	}

	/*public void createMybatisColumnConfig(String tableName) {
		StringBuffer buffer = new StringBuffer();
		if (dataList != null) {
			buffer.append("<resultMap id=\"BaseResultMap\" type=\"").append(tableName).append("\"> ").append("\r\n");
			for (Map<String, Object> map : dataList) {
				// <result column="CI_TYP" jdbcType="CHAR" property="ciTyp" />
				String columnName = (String) map.get("columnName");
				String fieldName = getFieldName(columnName);
				String jdbcType = getMybatisJdbcType((Integer) map.get("dataType"));

				if (primaryKeyList.contains(columnName)) {
					buffer.append("    <id column=\"").append(columnName).append("\" ").append("jdbcType=\"")
							.append(jdbcType).append("\" property=\"").append(fieldName).append("\" />").append("\r\n");
				} else {
					buffer.append("    <result column=\"").append(columnName).append("\" ").append("jdbcType=\"")
							.append(jdbcType).append("\" property=\"").append(fieldName).append("\" />").append("\r\n");
				}
			}
			buffer.append("</resultMap>").append("\r\n");
		}

		buffer.append("<sql id=\"BaseColumnList\">").append("\r\n");
		int length = dataList.size();
		int count = 0;
		buffer.append("    ");
		for (Map<String, Object> map : dataList) {
			count++;
			buffer.append(map.get("columnName"));
			if (count != length) {
				buffer.append(", ");
			}
		}
		buffer.append("\r\n");
		buffer.append("</sql>").append("\r\n");

		// insert配置
		buffer.append("<insert id=\"insert\" parameterType=\"\">").append("\r\n");
		buffer.append("    insert into ").append(getTableName()).append("\r\n");
		buffer.append("    <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">").append("\r\n");
		for (Map<String, Object> map : dataList) {
			String columnName = (String) map.get("columnName");
			String fieldName = getFieldName(columnName);
			buffer.append("        <if test=\"").append(fieldName).append(" != null\"> \r\n");
			buffer.append("            ").append(columnName).append(",").append("\r\n");
			buffer.append("        </if> \r\n");
		}
		buffer.append("    </trim>").append("\r\n");
		buffer.append("    <trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\"> \r\n");
		for (Map<String, Object> map : dataList) {
			String columnName = (String) map.get("columnName");
			String fieldName = getFieldName(columnName);
			String jdbcType = getMybatisJdbcType((Integer) map.get("dataType"));
			buffer.append("        <if test=\"").append(fieldName).append(" != null\"> \r\n");
			buffer.append("            #{").append(fieldName).append(",jdbcType=").append(jdbcType).append("}, \r\n");
			buffer.append("        </if> \r\n");
		}
		buffer.append("    </trim>").append("\r\n");

		// update配置
		buffer.append("<update id=\"update\" parameterType=\"java.util.Map\"> \r\n");
		buffer.append("    update ").append(getTableName()).append("\r\n");
		buffer.append("    <set>").append("\r\n");
		for (Map<String, Object> map : dataList) {
			String columnName = (String) map.get("columnName");
			String fieldName = getFieldName(columnName);
			String jdbcType = getMybatisJdbcType((Integer) map.get("dataType"));
			buffer.append("        <if test=\"").append(fieldName).append(" != null\"> \r\n");
			buffer.append("            ").append(columnName).append(" = ").append("#{").append(fieldName)
					.append(",jdbcType=").append(jdbcType).append("}, \r\n");
			buffer.append("        </if> \r\n");
		}
		buffer.append("    </set> \r\n");
		buffer.append("    where ").append("\r\n");

		// for (String primaryKey : primaryKeyList)
		// {
		// buffer.append(" ").append(primaryKey).append(" = #{lnNo,jdbcType=CHAR}");
		// }

		buffer.append("</update>");

		System.out.println(buffer.toString());
	}
*/
	
	public String getAddSql(String tableName) {
		StringBuffer buffer=new StringBuffer();
		buffer.append("\"<script>\",\r\n");
		buffer.append("    \"insert into ").append(tableName).append("\",\r\n");
		buffer.append("    \"<trim prefix=\\\"(\\\" suffix=\\\")\\\" suffixOverrides=\\\",\\\">\",").append("\r\n");
		for (Map<String, Object> map : dataList) {
			String columnName = (String) map.get("columnName");
			String fieldName = getFieldName(columnName);
			buffer.append("        \"<if test=\\\"").append(fieldName).append(" != null\\\">\", \r\n");
			buffer.append("            \"").append(columnName).append(",").append("\",\r\n");
			buffer.append("        \"</if>\", \r\n");
		}
		buffer.append("    \"</trim>\",").append("\r\n");
		buffer.append("    \"<trim prefix=\\\"values (\\\" suffix=\\\")\\\" suffixOverrides=\\\",\\\">\", \r\n");
		for (Map<String, Object> map : dataList) {
			String columnName = (String) map.get("columnName");
			String fieldName = getFieldName(columnName);
			String jdbcType = getMybatisJdbcType((Integer) map.get("dataType"));
			buffer.append("        \"<if test=\\\"").append(fieldName).append(" != null\\\">\", \r\n");
			buffer.append("            \"#{").append(fieldName).append(",jdbcType=").append(jdbcType).append("}, \",\r\n");
			buffer.append("        \"</if>\", \r\n");
		}
		buffer.append("    \"</trim>\",").append("\r\n");
		buffer.append("\"</script>\" \r\n");
		return buffer.toString();
	}
	

	private String getSelectListSql(String tableName) {
		StringBuffer buffer=new StringBuffer();
		buffer.append("     \"<script>\",\r\n");
		buffer.append("        \"select \",\r\n");
		
		int a=0;
		for (Map<String, Object> map : dataList) {
			if(a==0) {
				buffer.append("        \"");
			}
			String columnName = (String) map.get("columnName");
			String fieldName = getFieldName(columnName);
			buffer.append(" "+fieldName+",");
			a++;
			if(a==5) {
				buffer.append("\",\r\n");
				a=0;
			}
			
		}
		buffer.substring(0,buffer.length()-7);
		buffer.append("\",\r\n");
		buffer.append("        \"from "+tableName+" \",\r\n" + 
					  "        \"<where>\",\r\n" + 
					  "        \"<if test='belongToId!=null and belongToId !=\\\"\\\"'>\",\r\n" + 
					  "        \"</if>\",\r\n" + 
					  "        \"<if test='findKey!=null and findKey !=\\\"\\\"'>\",\r\n" + 
					  "        \"</if>\",\r\n" + 
					  "        \"</where>\",\r\n" + 
					  "        \"order by makeDate desc,makeTime desc limit #{offset},#{pageCount}\",\r\n"+
					  "        \"</script>\"\r\n");
		return buffer.toString();
	}

	private String getSelectCountSql(String tableName) {
		StringBuffer buffer=new StringBuffer();
		buffer.append("\"<script>\",\r\n" + 
				"		\"<bind name='pattern' value=\\\"'%'+findKey+'%'\\\" />\",\r\n" + 
				"    	\"select count(*) \",\r\n" + 
				"    	\"from "+tableName+" \",\r\n" + 
				"    	\"<where>\",\r\n" + 
				"    	\"<if test='belongToId!=null and belongToId !=\\\"\\\"'>\",\r\n" + 
				"    	\"</if>\",\r\n" + 
				"    	\"<if test='findKey!=null and findKey !=\\\"\\\"'>\",\r\n" + 
				"    	\"</if>\",\r\n" + 
				"    	\"</where>\",\r\n" + 
				"    	\"</script>\"");
		return buffer.toString();
	}

	private String getSelectByIdSql(String tableName) {
		StringBuffer buffer=new StringBuffer();
		buffer.append("     \"<script>\",\r\n");
		buffer.append("        \"select \",\r\n");
		
		int a=0;
		for (Map<String, Object> map : dataList) {
			if(a==0) {
			   buffer.append("        \"");
			}
			String columnName = (String) map.get("columnName");
			String fieldName = getFieldName(columnName);
			buffer.append(""+fieldName+",");
			a++;
			if(a==5) {
				buffer.append("\",\r\n");
				a=0;
			}
		}
		buffer.substring(0,buffer.length()-7);
		buffer.append("\",\r\n");
		buffer.append("        \"from "+tableName+"\",\r\n" + 
						"        \"<where>\",\r\n" + 
						"        \" id=#{id}\",\r\n" + 
						"        \"</where>\",\r\n" + 
						"        \"</script>\"\r\n");
		return buffer.toString();
	}

	private String getEditSql(String tableName) {
		// update配置
		StringBuffer buffer=new StringBuffer();
		buffer.append("\"<script>\",\r\n");
		buffer.append("    \"update ").append(tableName).append("\",\r\n");
		buffer.append("    \"<set>").append("\",\r\n");
		for (Map<String, Object> map : dataList) {
			String columnName = (String) map.get("columnName");
			String fieldName = getFieldName(columnName);
			String jdbcType = getMybatisJdbcType((Integer) map.get("dataType"));
			buffer.append("        \"<if test=\\\"").append(fieldName).append(" != null\\\">\", \r\n");
			buffer.append("            \"").append(columnName).append(" = ").append("#{").append(fieldName)
					.append(",jdbcType=").append(jdbcType).append("}\", \r\n");
			buffer.append("        \"</if>\", \r\n");
		}
		buffer.append("    \"</set>\", \r\n");
		buffer.append("    \"where  id = #{id} \", ").append("\r\n");
		buffer.append("\"</script>\" \r\n");
		return buffer.toString();
	}

	private String getDeleteSql(String tableName) {
		StringBuffer buffer=new StringBuffer();
		buffer.append("\"<script>\",\r\n" + 
				"    	\"delete from "+tableName+" where id in \",\r\n" + 
				"    	\"<foreach collection='list' item='item' open='(' separator=',' close=')'>\",\r\n" + 
				"        \"#{item}\",\r\n" + 
				"        \"</foreach>\",\r\n" + 
				"    	\"</script>\" \r\n");
		return buffer.toString();
	}
	
	/**
	 * 根据列的类型，获取mybatis配置中的jdbcType
	 * 
	 * @param dataType
	 *            列的类型
	 * @return String jdbcType
	 */
	private static String getMybatisJdbcType(int dataType) {
		String jdbcType = "";
		if (dataType == Types.TINYINT) {
			jdbcType = "TINYINT";
		} else if (dataType == Types.SMALLINT) {
			jdbcType = "SMALLINT";
		} else if (dataType == Types.INTEGER) {
			jdbcType = "INTEGER";
		} else if (dataType == Types.BIGINT) {
			jdbcType = "BIGINT";
		} else if (dataType == Types.FLOAT) {
			jdbcType = "FLOAT";
		} else if (dataType == Types.DOUBLE) {
			jdbcType = "DOUBLE";
		} else if (dataType == Types.DECIMAL) {
			jdbcType = "DECIMAL";
		} else if (dataType == Types.NUMERIC) {
			jdbcType = "NUMERIC";
		} else if (dataType == Types.VARCHAR) {
			jdbcType = "VARCHAR";
		} else if (dataType == Types.NVARCHAR) {
			jdbcType = "NVARCHAR";
		} else if (dataType == Types.CHAR) {
			jdbcType = "CHAR";
		} else if (dataType == Types.NCHAR) {
			jdbcType = "NCHAR";
		} else if (dataType == Types.CLOB) {
			jdbcType = "CLOB";
		} else if (dataType == Types.BLOB) {
			jdbcType = "BLOB";
		} else if (dataType == Types.NCLOB) {
			jdbcType = "NCLOB";
		} else if (dataType == Types.DATE) {
			jdbcType = "DATE";
		} else if (dataType == Types.TIMESTAMP) {
			jdbcType = "TIMESTAMP";
		} else if (dataType == Types.ARRAY) {
			jdbcType = "ARRAY";
		} else if (dataType == Types.TIME) {
			jdbcType = "TIME";
		} else if (dataType == Types.BOOLEAN) {
			jdbcType = "BOOLEAN";
		} else if (dataType == Types.BIT) {
			jdbcType = "BIT";
		} else if (dataType == Types.BINARY) {
			jdbcType = "BINARY";
		} else if (dataType == Types.OTHER) {
			jdbcType = "OTHER";
		} else if (dataType == Types.REAL) {
			jdbcType = "REAL";
		} else if (dataType == Types.LONGVARCHAR) {
			jdbcType = "LONGVARCHAR";
		} else if (dataType == Types.VARBINARY) {
			jdbcType = "VARBINARY";
		} else if (dataType == Types.LONGVARBINARY) {
			jdbcType = "LONGVARBINARY";
		}

		return jdbcType;
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	// 通过数据库来得到所有表名
	public List<String> getAllTableName() {
		List<String> tableNameS = null;
		Connection conn = null;
		try {
			conn = getConnection();
			tableNameS = new ArrayList<String>();
			DatabaseMetaData dbmd = conn.getMetaData();

			ResultSet rs = dbmd.getTables(null, null, null, new String[] { "TABLE" });
			while (rs.next()) {
				// 获得表名
				String table_name = rs.getString("TABLE_NAME");
				tableNameS.add(table_name);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return tableNameS;
	}
	
	//根据连接获取数据库名
	public String getDataBaseName(String url) {
	    String beforeValue=url.substring(0, url.indexOf("?"));
	    beforeValue=beforeValue.replace("//", "*");
	    return beforeValue.substring(beforeValue.indexOf("/")+1);
	}
	
	
	//获取表的注释
	public void getAllTableComment(String tableNameParam) {
		//获取数据库名
		String dataBaseName=getDataBaseName(dbUrl);
		Connection conn = null;
		try {
			conn = getConnection();
			PreparedStatement pStemt = null;
			ResultSet rs = null;
			pStemt = (PreparedStatement)conn.prepareStatement("Select table_name,TABLE_COMMENT  from INFORMATION_SCHEMA.TABLES Where table_schema = '"+dataBaseName+"' and table_name ='"+tableNameParam+"'");
			rs=pStemt.executeQuery();
			while (rs.next()) {
				String tableName=rs.getString(1);
				String tableComment=rs.getString(2);
				tableCommentMap.put(tableName, tableComment);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		String a="com\\zzd\\ptt";
		String b=a.replaceAll("\\\\",".");
		System.out.println(b);
		
	}
}
//                