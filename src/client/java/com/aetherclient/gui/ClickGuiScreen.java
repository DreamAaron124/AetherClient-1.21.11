package com.aetherclient.gui;

import com.aetherclient.AetherClient;
import com.aetherclient.config.ClientConfig.ModuleConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class ClickGuiScreen extends Screen {
    private int selected = 0;
    private boolean draggingOpacity = false;
    private boolean draggingRange = false;
    private boolean draggingLine = false;
    private final String[] modules = {"Storage Finder", "Spawner Finder"};

    public ClickGuiScreen() { super(Text.literal("Aether Client")); }
    private ModuleConfig cfg() { return selected == 0 ? AetherClient.CONFIG.storage : AetherClient.CONFIG.spawner; }

    @Override
    protected void init() { }

    @Override
    public void render(DrawContext d, int mouseX, int mouseY, float delta) {
        int w=width,h=height;
        d.fill(0,0,w,h,0x8805080D);
        int x=Math.max(50,w/2-430), y=Math.max(35,h/2-255), pw=Math.min(860,w-100), ph=Math.min(510,h-70);
        d.fill(x,y,x+pw,y+ph,0xFF0E121A);
        d.fill(x,y,x+pw,y+54,0xFF121925);
        d.fill(x,y,x+4,y+ph,0xFF6FB7FF);
        text(d,"AETHER",x+24,y+17,0xFFEAF4FF,2);
        text(d,"CLIENT",x+84,y+18,0xFF6FB7FF,1);
        text(d,"1.21.11  •  FABRIC",x+pw-150,y+21,0xFF738096,0);

        int sideW=190;
        d.fill(x,y+54,x+sideW,y+ph,0xFF0A0E15);
        text(d,"MODULES",x+20,y+78,0xFF68748A,0);
        for(int i=0;i<modules.length;i++) {
            int by=y+98+i*48;
            boolean sel=i==selected;
            if(sel) d.fill(x+12,by,x+sideW-12,by+38,0xFF172231);
            if(sel) d.fill(x+12,by,x+15,by+38,0xFF6FB7FF);
            text(d,modules[i],x+28,by+11,sel?0xFFE8F2FF:0xFF8C97AA,sel?1:0);
            text(d, i==0 ? "▣" : "✦", x+sideW-32,by+11,sel?0xFF6FB7FF:0xFF566173,1);
        }
        text(d,"CONFIG",x+20,y+230,0xFF68748A,0);
        text(d,"Saved automatically",x+20,y+252,0xFF556073,0);
        text(d,"Right Shift  •  close",x+20,y+274,0xFF556073,0);

        int cx=x+sideW+26, cy=y+78;
        text(d,modules[selected],cx,cy,0xFFF2F6FC,1);
        text(d,selected==0?"Locate storage blocks through the world":"Locate mob spawners through the world",cx,cy+20,0xFF778398,0);
        drawToggle(d,cfg(),cx+pw-sideW-88,cy-2);

        section(d,"RENDER",cx,cy+58,pw-sideW-52);
        label(d,"Mode",cx,cy+92);
        button(d,cfg().mode,cx+118,cy+80,128,30);
        label(d,"Range",cx,cy+136); slider(d,cx+118,cy+148,250,cfg().range,8,128,draggingRange);
        label(d,"Outline",cx,cy+184); slider(d,cx+118,cy+196,250,Math.round(cfg().outlineOpacity*100),0,100,draggingOpacity);
        label(d,"Fill",cx,cy+232); slider(d,cx+118,cy+244,250,Math.round(cfg().fillOpacity*100),0,100,false);
        label(d,"Line width",cx,cy+280); slider(d,cx+118,cy+292,250,Math.round(cfg().lineWidth*10),5,40,draggingLine);

        section(d,"COLOR",cx,cy+336,pw-sideW-52);
        label(d,"R",cx,cy+370); slider(d,cx+48,cy+382,95,cfg().red,0,255,false);
        label(d,"G",cx+170,cy+370); slider(d,cx+218,cy+382,95,cfg().green,0,255,false);
        label(d,"B",cx+340,cy+370); slider(d,cx+388,cy+382,95,cfg().blue,0,255,false);
        d.fill(cx+500,cy+374,cx+pw-sideW-38,cy+410,(cfg().red<<16)|(cfg().green<<8)|cfg().blue);
        text(d,"LIVE",cx+510,cy+388,0xFFFFFFFF,1);

        text(d,"Storage: chest • trapped chest • barrel • shulker • hopper • dropper • dispenser",cx,y+ph-26,0xFF536074,0);
        super.render(d,mouseX,mouseY,delta);
    }

    private void section(DrawContext d,String s,int x,int y,int width){ text(d,s,x,y,0xFF657187,0); d.fill(x+70,y+5,x+width,y+6,0xFF202A38); }
    private void label(DrawContext d,String s,int x,int y){ text(d,s,x,y,0xFFB2BDCC,0); }
    private void button(DrawContext d,String s,int x,int y,int w,int h){ d.fill(x,y,x+w,y+h,0xFF172231); d.fill(x,y,x+w,y+1,0xFF2B3B51); text(d,s,x+12,y+10,0xFFDAE6F5,0); text(d,"‹ ›",x+w-26,y+10,0xFF6F7E94,0); }
    private void drawToggle(DrawContext d,ModuleConfig c,int x,int y){ d.fill(x,y,x+54,y+26,c.enabled?0xFF477FBD:0xFF293241); d.fill(x+(c.enabled?30:4),y+4,x+(c.enabled?50:24),y+22,0xFFF2F6FC); }
    private void slider(DrawContext d,int x,int y,int width,int value,int min,int max,boolean active){
        d.fill(x,y+6,x+width,y+9,0xFF293342); int px=x+(int)((value-min)/(double)(max-min)*width); d.fill(x,y+6,px,y+9,0xFF6FB7FF); d.fill(px-5,y+1,px+5,y+14,active?0xFFFFFFFF:0xFFB8D9FF); text(d,String.valueOf(value),x+width+10,y+2,0xFF77859A,0);
    }
    private void text(DrawContext d,String s,int x,int y,int color,int bold){ d.drawTextWithShadow(textRenderer,Text.literal(s),x,y,color); }

    @Override
    public boolean mouseClicked(double mx,double my,int button) {
        int x=Math.max(50,width/2-430), y=Math.max(35,height/2-255), sideW=190, pw=Math.min(860,width-100);
        if(button!=0) return super.mouseClicked(mx,my,button);
        if(mx>=x+12&&mx<=x+sideW-12) for(int i=0;i<modules.length;i++){ int by=y+98+i*48; if(my>=by&&my<=by+38){selected=i; return true;} }
        int cx=x+sideW+26, cy=y+78;
        if(mx>=x+pw-sideW-88&&mx<=x+pw-sideW-34&&my>=cy-2&&my<=cy+24){cfg().enabled=!cfg().enabled;AetherClient.saveConfig();return true;}
        if(mx>=cx+118&&mx<=cx+246&&my>=cy+80&&my<=cy+110){ cfg().mode=nextMode(cfg().mode); AetherClient.saveConfig(); return true; }
        if(my>=cy+140&&my<=cy+164){draggingRange=true;updateSlider(mx,cx+118,250,8,128);return true;}
        if(my>=cy+188&&my<=cy+212){draggingOpacity=true;updateSlider(mx,cx+118,250,0,100);return true;}
        if(my>=cy+288&&my<=cy+312){draggingLine=true;updateSlider(mx,cx+118,250,5,40);return true;}
        if(my>=cy+376&&my<=cy+402){
            if(mx>=cx+48&&mx<=cx+143){cfg().red=(int)Math.round(Math.max(0,Math.min(1,(mx-(cx+48))/95.0))*255);}
            else if(mx>=cx+218&&mx<=cx+313){cfg().green=(int)Math.round(Math.max(0,Math.min(1,(mx-(cx+218))/95.0))*255);}
            else if(mx>=cx+388&&mx<=cx+483){cfg().blue=(int)Math.round(Math.max(0,Math.min(1,(mx-(cx+388))/95.0))*255);}
            AetherClient.saveConfig(); return true;
        }
        return true;
    }
    private String nextMode(String s){return switch(s){case "OUTLINE"->"CORNERS";case "CORNERS"->"FULL_BOX";default->"OUTLINE";};}
    private void updateSlider(double mx,int x,int width,int min,int max){double t=Math.max(0,Math.min(1,(mx-x)/(double)width));int v=(int)Math.round(min+t*(max-min)); if(min==8)cfg().range=v; else if(min==0)cfg().outlineOpacity=v/100f; else cfg().lineWidth=v/10f; AetherClient.saveConfig();}
    @Override public boolean mouseDragged(double mx,double my,int button,double dx,double dy){if(button==0){int x=Math.max(50,width/2-430),cx=x+216,cy=Math.max(35,height/2-255)+78;if(draggingRange)updateSlider(mx,cx-98,250,8,128);if(draggingOpacity)updateSlider(mx,cx-98,250,0,100);if(draggingLine)updateSlider(mx,cx-98,250,5,40);return true;}return super.mouseDragged(mx,my,button,dx,dy);}
    @Override public boolean mouseReleased(double mx,double my,int button){draggingRange=draggingOpacity=draggingLine=false;return super.mouseReleased(mx,my,button);}
    @Override public boolean shouldPause(){return false;}
}
