package meme.server;

import java.io.File;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;

public class ScreenShotter {

	private static final String[] VLC_ARGS = {
        "--intf=dummy",          /* no interface */
        "--vout=dummy",          /* we don't want video (output) */
        "--no-audio",               /* we don't want audio (decoding) */
        "--no-osd",
        "--no-spu",
        "--no-stats",               /* no stats */
        "--no-sub-autodetect-file", /* we don't want subtitles */
        "--no-disable-screensaver", /* we don't want interfaces */
        "--no-snapshot-preview",    /* no blending in dummy vout */
    };

	static int width = 150;
	
	static float pos = 0.01f;
	
	public static String getScreenShot(String filepath){
		
		String relpath = "../"+filepath;
		System.out.println(filepath); 
		
		// check screen shot exists
		File fpicture = new File(relpath+".png");
		if(fpicture.exists() && !fpicture.isDirectory()) {
			System.out.println("Screen shot already exists for " + relpath);
			return relpath+".png";
		}
		
		// check if video exist
		File fvideo = new File(relpath);
		if(!fvideo.exists() || fvideo.isDirectory()) {
			System.out.println("Video doesn't exist at " + relpath);
			return null;
		}
		
		MediaPlayerFactory factory = new MediaPlayerFactory(VLC_ARGS);
        MediaPlayer mediaPlayer = factory.newHeadlessMediaPlayer();
        
        mediaPlayer.startMedia(relpath);
        mediaPlayer.setPosition(pos);
        try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        File file = new File(relpath+".png");
        mediaPlayer.saveSnapshot(file, width, 0);
        mediaPlayer.release();
        
        return relpath+".png";
	}
}
