package railo.runtime.tag;


import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.ReferenceMap;

import railo.commons.color.ColorCaster;
import railo.commons.io.res.Resource;
import railo.commons.io.res.type.file.FileResource;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.BodyTagSupport;
import railo.runtime.functions.string.JSStringFormat;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Struct;
import railo.runtime.video.VideoInput;
import railo.runtime.video.VideoInputImpl;
import railo.runtime.video.VideoUtilImpl;

/**
 * implementation of the tag Compiler
 */
public class VideoPlayerJW extends BodyTagSupport {

	private static final int TYPE_NONE = 0;
	private static final int TYPE_PLAYLIST = 1;
	private static final int TYPE_CHAPTERS = 2;
	
	private static final int PLAYLIST_NONE = 0;
	private static final int PLAYLIST_RIGHT = 1;
	private static final int PLAYLIST_BOTTOM = 2;
	
	private static Color BG_COLOR=new Color(51,51,51);
	private static Color FG_COLOR=new Color(198,198,198);
	
	private String video=null;
	private boolean autostart=false;
	
	private railo.runtime.video.Range showPlay=railo.runtime.video.Range.TRUE;
	private railo.runtime.video.Range showPause=railo.runtime.video.Range.TRUE;
	private railo.runtime.video.Range showTimeline=railo.runtime.video.Range.TRUE;
	private List params=new ArrayList();
	private java.awt.Color bgcolor=BG_COLOR;
	private java.awt.Color fgcolor=FG_COLOR;
	private java.awt.Color screencolor=null;
	private java.awt.Color lightcolor=null;
	
	
	
	private int width=-1;
	private int height=-1;
	private boolean debug;
	private boolean allowfullscreen;
	private String strWidth;
	private String strHeight;
	private static Map sizes=new ReferenceMap(ReferenceMap.SOFT,ReferenceMap.SOFT);
	
	// JW
	private Struct passthrough=null;
	private String preview;
	
	private boolean  group=false;
	private boolean playlistThumbnails=true;
	private int playlistSize=-1;
	private int playlist=PLAYLIST_NONE;
	private String target="_self";
	private boolean linkfromdisplay;
	private String overstretch;
	private boolean download;
	private String id;
	private String align;
	private static int _id=0;

	public VideoPlayerJW()  {
		
	}
	
	/**
	 * @see javax.servlet.jsp.tagext.Tag#release()
	 */ 
	public void release() {
		super.release();
		video=null;
		autostart=false;
		
		showPlay=railo.runtime.video.Range.TRUE;
		showPause=railo.runtime.video.Range.TRUE;
		showTimeline=railo.runtime.video.Range.TRUE;
		params.clear();
		debug=false;
		
		id=null;
		group=false;
		playlist = PLAYLIST_NONE;
		playlistSize=-1;
		playlistThumbnails=true;
		target="_self";
		linkfromdisplay=false;
		overstretch=null;
		/*group="yes"
			playlist="right,bottom,none"
			playlistSize="300"
			playlistThumbnails="300"
		
		*/
		align=null;
		
		
		bgcolor=BG_COLOR;
		fgcolor=FG_COLOR;
		screencolor=null;
		lightcolor=null;
		width=-1;
		height=-1;

		strWidth=null;
		strHeight=null;
		
		// JW
		passthrough=null;
		preview=null;
		allowfullscreen=false;
		download=false;
	}



	protected void setParam(VideoPlayerParamBean param) {
		params.add(param);
	}

	/**
	 * @param video the video to set
	 */
	public void setVideo(String video) {
		this.video = video;
	}

	/**
	 * @param autostart the autostart to set
	 */
	public void setAutostart(boolean autostart) {
		this.autostart = autostart;
	}

	/**
	 * @param showPlay the showPlay to set
	 * @throws PageException 
	 */
	public void setShowplay(String showPlay) throws PageException {
		this.showPlay = railo.runtime.video.Range.toRange(showPlay);
	}
	public void setId(String id) throws PageException {
		this.id=Caster.toVariableName(id);
	}

	/**
	 * @param showPause the showPause to set
	 * @throws PageException 
	 */
	public void setShowpause(String showPause) throws PageException {
		this.showPause = railo.runtime.video.Range.toRange(showPause);
	}

	/**
	 * @param showTimeline the showTimeline to set
	 * @throws PageException 
	 */
	public void setShowtimeline(String showTimeline) throws PageException {
		this.showTimeline = railo.runtime.video.Range.toRange(showTimeline);
	}

	/**
	 * @param color the background color to set
	 * @throws PageException 
	 */
	public void setBgcolor(String color) throws PageException {
		this.bgcolor = ColorCaster.toColor(color);
	}
	public void setBackgroundcolor(String color) throws PageException {
		setBgcolor(color);
	}
	public void setBackground(String color) throws PageException {
		setBgcolor(color);
	}
	public void setScreencolor(String color) throws PageException {
		this.screencolor = ColorCaster.toColor(color);
	}
	public void setLightcolor(String color) throws PageException {
		this.lightcolor = ColorCaster.toColor(color);
	}
	

	/**
	 * @param color the background color to set
	 * @throws PageException 
	 */
	public void setFgcolor(String color) throws PageException {
		this.fgcolor = ColorCaster.toColor(color);
	}
	
	public void setForeground(String color) throws PageException {
		setFgcolor(color);
	}
	
	public void setForegroundcolor(String color) throws PageException {
		setFgcolor(color);
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(String strWidth) {
		this.strWidth = strWidth;
		this.width = Caster.toIntValue(strWidth,-1);
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(String strHeight) {
		this.strHeight = strHeight;
		this.height = Caster.toIntValue(strHeight,-1);
	}

	/**
	 * @throws IOException 
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws PageException {
		return EVAL_BODY_INCLUDE;
	}
	public int doEndTag() throws PageException {

		// fill top video to params
		if(video!=null) {
			VideoPlayerParamBean vppb = new VideoPlayerParamBean();
			vppb.setVideo(pageContext,video);
			if(!StringUtil.isEmpty(preview))vppb.setImage(pageContext,preview);
			params.add(vppb);
		}
		else {
			if(!StringUtil.isEmpty(preview))
				throw new ApplicationException("attribute [preview] is only allowed when attribute [video] is used");
		}
		
		if(params.size()==0)
			throw new ApplicationException("you have to define at least one video source");
		
		
		
		
		
		// calculate dimension
		int[] dim = calculateDimension(pageContext,params,width,strWidth,height,strHeight);

		//print.out(width+":"+height);
		//print.out(strWidth+":"+strHeight);
		width=dim[0];
		height=dim[1];
		
		//print.out(width+":"+height);
		
		
		// playlist
		int dspHeight=-1,dspWidth=-1;
		if(playlist!=PLAYLIST_NONE) {
			if(playlistSize<20)playlistSize=playlist==PLAYLIST_BOTTOM?100:200; 
			if(playlist==PLAYLIST_BOTTOM) {
				dspHeight=height;
				height+=playlistSize;
			}
			else {
				dspWidth=width;
				width+=playlistSize;
			}
		}
		else playlistThumbnails=false;
		
		
		VideoPlayerParamBean param;
			
		
		String id=getId();//
		String placeholderId="ph_"+id;
		String flashId="swf_"+id;
		
		StringBuffer sb=new StringBuffer();
		
		write(sb,"<script type=\"text/javascript\" src=\"/railo-context/swfobject.js.cfm\"></script>");
		write(sb,"<div ");			
		
		
		if(passthrough!=null) {
			Key[] keys = passthrough.keys();
			String key;
			for(int i=0;i<keys.length;i++) {
				key=keys[i].getString();
				if(StringUtil.startsWithIgnoreCase(key, "div."))
					write(sb,key.substring(4)+"=\""+Caster.toString(passthrough.get(keys[i]))+"\" ");
			}
		}
		write(sb,(align!=null?"align=\""+align+"\"":"")+" id=\""+placeholderId+"\"><a href=\"http://www.macromedia.com/go/getflashplayer\">Get the Flash Player</a> to see this player.</a></div>");			
		
		
		
		write(sb,"<script type=\"text/javascript\">\n");			
		write(sb,"var so = new SWFObject(\"/railo-context/mediaplayer.swf.cfm\", \""+flashId+"\", \""+width+"\", \""+(height)+"\", \"8\", \""+format("#",bgcolor)+"\");\n");			
		
		// script
		addParam(sb,"allowscriptaccess","always");
		addVariable(sb,"enablejs","true");
		addVariable(sb,"javascriptid",flashId);
		
		addVariable(sb,"shuffle","false");
		addVariable(sb,"linktarget",target);
		addVariable(sb,"linkfromdisplay",Caster.toString(linkfromdisplay));
		addVariable(sb,"abouttxt","Railo Video Player");
		addVariable(sb,"aboutlnk","http://www.getrailo.org");
		
		// control
		addParam(sb,"allowfullscreen",Caster.toString(allowfullscreen));
		addParam(sb,"usefullscreen",Caster.toString(allowfullscreen));
		addVariable(sb,"autostart",Caster.toString(autostart));
		if(!StringUtil.isEmpty(overstretch))addVariable(sb,"overstretch",overstretch);
		addVariable(sb,"showdownload",Caster.toString(download));
		
		
		
		
		// color
		if(lightcolor==null)lightcolor=fgcolor.brighter();
		if(screencolor==null)screencolor=Color.BLACK;//fgcolor.brighter();
		addVariable(sb,"backcolor",format("0x",bgcolor));
		addVariable(sb,"frontcolor",format("0x",fgcolor));
		addVariable(sb,"lightcolor",format("0x",lightcolor));
		addVariable(sb,"screencolor",format("0x",screencolor));
		
		if(passthrough!=null) {
			Key[] keys = passthrough.keys();
			String key;
			for(int i=0;i<keys.length;i++) {
				key=keys[i].getString();
				if(StringUtil.startsWithIgnoreCase(key, "param."))
					addParam(sb,key.substring(6),Caster.toString(passthrough.get(keys[i])));
				else if(StringUtil.startsWithIgnoreCase(key, "variable."))
					addVariable(sb,key.substring(9),Caster.toString(passthrough.get(keys[i])));
				else if(StringUtil.startsWithIgnoreCase(key, "div."));
				else
					addVariable(sb,key,Caster.toString(passthrough.get(keys[i])));
			}
		}

		if(params.size()>1 && group) addVariable(sb,"repeat","true");
		
		/*if(playlist!=PLAYLIST_NONE) {
			if(playlistSize<20)playlistSize=playlist==PLAYLIST_BOTTOM?300:200; 
			if(playlist==PLAYLIST_BOTTOM) {
				addVariable(sb,"displayheight",Caster.toString(height));
				height+=playlistSize;
			}
			else {
				addVariable(sb,"displaywidth",Caster.toString(width));
				width+=playlistSize;
			}
			if(playlistThumbnails && hasImages())addVariable(sb,"thumbsinplaylist","true");
		}*/

		// dimension
		if(dspWidth>0)addVariable(sb,"displaywidth",Caster.toString(dspWidth));
		if(dspHeight>0)addVariable(sb,"displayheight",Caster.toString(dspHeight));
		addVariable(sb,"width",Caster.toString(width));
		addVariable(sb,"height",Caster.toString(height));
		if(playlistThumbnails && hasImages())addVariable(sb,"thumbsinplaylist","true");
		
		//if(preview!=null) addVariable(sb,"image",toPath(preview));
		//Iterator it = params.iterator();
		//addVariable("file","/rvp/videos/David.flv");
		//addVariable("captions","http://localhost:8080/caption.cfm");
		//while(it.hasNext()) {
			//param=(VideoPlayerParamBean) it.next();
			//addVariable(sb,"file",toPath(param.getResource()));
			//break;	
		//}
		//addVariable("image","video.jpg");
		
		write(sb,"so.write(\""+placeholderId+"\");\n");
		//if(params.size()>1) {
		Iterator it = params.iterator();
		while(it.hasNext()) {
			param=(VideoPlayerParamBean) it.next();
			addItem(sb,flashId,param);
		}
		//}
		write(sb,"</script>");
		try {
			if(debug) {
				pageContext.forceWrite("<pre>"+StringUtil.replace(sb.toString(), "<", "&lt;", false)+"</pre>");
			}
			pageContext.forceWrite(sb.toString());
			
			
		} 
		catch (IOException e) {
			
		}
	    return EVAL_PAGE;
	}

	private synchronized String getId() {
		if(!StringUtil.isEmpty(id)) return id;
		_id++;
		if(_id<0) _id=1;
		return ""+_id; 
	}

	private boolean hasImages() {
		Iterator it = params.iterator();
		while(it.hasNext()) {
			if(((VideoPlayerParamBean) it.next()).getImage()!=null)	return true;
		}
		return false;
	}

	private void addItem(StringBuffer sb, String id, VideoPlayerParamBean param) {
		//sb.append("setTimeout('thisMovie(\""+id+"\").addItem({file:\""+JSStringFormat.invoke(path)+"\"},null);',1000);\n");
		
		// file
		String file = "file:'"+JSStringFormat.invoke(toPath(param.getResource()))+"'";
		
		// image
		String image="";
		if(param.getImage()!=null) {
			image=",image:'"+JSStringFormat.invoke(toPath(param.getImage()))+"'";
		}
		
		// title
		String title="";
		if(!StringUtil.isEmpty(param.getTitle())) {
			title=",title:'"+JSStringFormat.invoke(param.getTitle())+"'";
		}
		
		// link
		String link="";
		if(!StringUtil.isEmpty(param.getLink())) {
			link=",link:'"+JSStringFormat.invoke(param.getLink())+"'";
		}
		
		// author
		String author="";
		if(!StringUtil.isEmpty(param.getAuthor())) {
			author=",author:'"+JSStringFormat.invoke(param.getAuthor())+"'";
		}
		
		sb.append("addItem('"+id+"',{"+file+title+image+link+author+"});\n");
	}

	private void addVariable(StringBuffer sb, String name, String value) {
		value=JSStringFormat.invoke(value);
		if(!(value.equals("false") || value.equals("true")))
			value="'"+value+"'";
		sb.append("so.addVariable('"+JSStringFormat.invoke(name)+"',"+value+");\n");
	}

	private void addParam(StringBuffer sb,String name, String value) {
		sb.append("so.addParam('"+name+"','"+value+"');\n");
	}

	private static int[] calculateDimension(PageContext pc,List params,int width, String strWidth,int height, String strHeight) throws PageException {
		Iterator it = params.iterator();
		ArrayList sources=new ArrayList();
		//Resource[] sources=new Resource[params.size()];
		VideoPlayerParamBean param;
		
		while(it.hasNext()) {
			param = (VideoPlayerParamBean) it.next();
			if(param.getVideo()!=null)
				sources.add(new VideoInputImpl(param.getVideo()));
		}
		return VideoUtilImpl.getInstance().calculateDimension(pc, (VideoInput[])sources.toArray(new VideoInput[sources.size()]), width, strWidth, height, strHeight);
		
	}

	private String toPath(Resource res) {
			if(!(res instanceof FileResource)) return res.getAbsolutePath();
			
			//Config config=pageContext.getConfig();
			PageSource ps = pageContext.toPageSource(res,null);
			if(ps==null) return res.getAbsolutePath();
			
			String realPath = ps.getRealpath();
			realPath=realPath.replace('\\', '/');
			if(realPath.endsWith("/"))realPath=realPath.substring(0,realPath.length()-1);
			
			//print.out("real:"+realPath);
			String mapping=ps.getMapping().getVirtual();
			mapping=mapping.replace('\\', '/');
			if(mapping.endsWith("/"))mapping=mapping.substring(0,mapping.length()-1);
			
			return mapping+realPath;
		
		
	}



	private void write(StringBuffer sb, String string) {
		sb.append(string);
	}



	private static String format(String prefix, Color color) {
		return prefix+toHex(color.getRed())+toHex(color.getGreen())+toHex(color.getBlue());
	}



	private static String toHex(int value) {
		String str = Integer.toHexString(value);
		if(str.length()==1) return "0".concat(str);
		return str;
	}

	/**
	 * @param debug the debug to set
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/**
	 * @param passthrough the passthrough to set
	 */
	public void setPassthrough(Struct passthrough) {
		this.passthrough = passthrough;
	}

	/**
	 * @param preview the preview to set
	 * @throws ExpressionException 
	 */
	public void setPreview(String preview) {
		this.preview = preview;//ResourceUtil.toResourceExisting(pageContext, preview);
	}

	/**
	 * @param allowfullscreen the allowfullscreen to set
	 */
	public void setAllowfullscreen(boolean allowfullscreen) {
		this.allowfullscreen = allowfullscreen;
	}
	
	public void setAlign(String strAlign) throws ApplicationException {
		if(StringUtil.isEmpty(strAlign)) return;
		strAlign=strAlign.trim().toLowerCase();
		if("right".equals(strAlign)) this.align = "right";
		else if("center".equals(strAlign)) this.align = "center";
		else if("left".equals(strAlign)) this.align = "left";
		else 
			throw new ApplicationException("invalid value for attribute align ["+strAlign+"], valid values are [left,center,right]");
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(boolean group) {
		this.group = group;
	}
	public void setLinktarget(String target)  {
		this.target = target;
	}
	public void setTarget(String target)  {
		this.target = target;
	}
	public void setLinkfromdisplay(boolean linkfromdisplay)  {
		this.linkfromdisplay = linkfromdisplay;
	}

	/**
	 * @param playlistThumbnails the playlistThumbnails to set
	 */
	public void setPlaylistthumbnails(boolean playlistThumbnails) {
		this.playlistThumbnails = playlistThumbnails;
	}
	public void setThumbnails(boolean playlistThumbnails) {
		setPlaylistthumbnails(playlistThumbnails);
	}
	public void setThumbs(boolean playlistThumbnails) {
		setPlaylistthumbnails(playlistThumbnails);
	}

	/**
	 * @param playlistSize the playlistSize to set
	 */
	public void setPlaylistsize(double playlistSize) throws ApplicationException {
		if(playlistSize<=40) throw new ApplicationException("playlist size has to be a positive number, at least 41px");
		this.playlistSize = (int) playlistSize;
	}

	/**
	 * @param playlist the playlist to set
	 */
	public void setPlaylist(String strPlaylist) throws PageException {
		strPlaylist=strPlaylist.trim().toLowerCase();
		if("right".equals(strPlaylist)) 		playlist=PLAYLIST_RIGHT;
		else if("bottom".equals(strPlaylist)) 	playlist=PLAYLIST_BOTTOM;
		else if("none".equals(strPlaylist)) 	playlist=PLAYLIST_NONE;
		else if(Decision.isBoolean(strPlaylist)) {
			playlist=Caster.toBooleanValue(strPlaylist)?PLAYLIST_BOTTOM:PLAYLIST_NONE;
		}
		else throw new ApplicationException("invalid playlist definition ["+strPlaylist+"], valid values are [right,bottom,none]");
	}

	/**
	 * @param overstretch the overstretch to set
	 */
	public void setOverstretch(String overstretch) throws PageException {
		overstretch=overstretch.trim().toLowerCase();
		if("fit".equals(overstretch)) 		overstretch="fit";
		else if("none".equals(overstretch)) overstretch="none";
		else if("proportion".equals(overstretch)) overstretch="true";
		else if(Decision.isBoolean(overstretch)) {
			overstretch=Caster.toString(Caster.toBooleanValue(overstretch));
		}
		else throw new ApplicationException("invalid overstretch definition ["+overstretch+"], valid values are [fit,none,true,false]");
		
		this.overstretch = overstretch;
	}

	/**
	 * @param download the download to set
	 */
	public void setDownload(boolean download) {
		this.download = download;
	}
	
}