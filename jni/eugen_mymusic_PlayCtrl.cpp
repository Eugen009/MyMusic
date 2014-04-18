#include <jni.h>
#include "eugen_mymusic_playctrl.h"
#include "fmod_errors.h"
#include <stdarg.h>
#include <stdio.h>
#include <unistd.h>
#include "fmod.hpp"
#include <pthread.h>
#include "MyHelper.h"
#include <assert.h>
#include <vector>
#include <string>

static FMOD::System* g_pSystem = 0;
static std::vector<FMOD::Channel*> m_Channels;
static std::vector<FMOD::Sound*> m_Sounds;
static FMOD::Channel* g_pCurChannel = 0;
static FMOD::Sound* g_pSound = 0;

void ERRCHECK( FMOD_RESULT res ){
	if( res != FMOD_OK ){
		LOG_ERROR( "Fmod error %d - %s", res, FMOD_ErrorString(res) );
		assert( false );
	}
}

FMOD_RESULT F_CALLBACK MyMusicCallback(FMOD_CHANNELCONTROL*,
					FMOD_CHANNELCONTROL_TYPE,
					FMOD_CHANNELCONTROL_CALLBACK_TYPE,
					void *, void *);

void InitFmodSystem(){
	if( g_pSystem != 0 )
		return;
    FMOD_RESULT       result;
    unsigned int      version = 0;
    void             *extradriverdata = 0;
    result = FMOD::System_Create(&g_pSystem);
    ERRCHECK(result);
    result = g_pSystem->getVersion(&version);
    ERRCHECK(result);
    if (version < FMOD_VERSION)
    {
		LOG_ERROR("FMOD lib version %08x doesn't match header version %08x", version, FMOD_VERSION );
    	return;
    }
    result = g_pSystem->init(32, FMOD_INIT_NORMAL, extradriverdata);
    ERRCHECK(result);
}

void DeInitFmodSystem()
{
	if( g_pSystem == 0 )
		return;
    FMOD_RESULT       result;
    for( size_t i = 0; i< m_Sounds.size(); i++ ){
    	m_Sounds[i] ->release();
    }
    m_Sounds.clear();

    result = g_pSystem->close();
    ERRCHECK(result);
    result = g_pSystem->release();
    ERRCHECK(result);
    g_pSystem = 0;
}

void Java_eugen_mymusic_PlayCtrl_setStateStart
  (JNIEnv *env, jobject thiz){
	InitFmodSystem();
	if( g_pSystem ){
		g_pSystem ->mixerResume();
	}
	return;
}

void Java_eugen_mymusic_PlayCtrl_setStateStop
  (JNIEnv *env, jobject thiz){
	if( g_pSystem ){
		g_pSystem ->mixerSuspend();
	}
  }

void Java_eugen_mymusic_PlayCtrl_setStateDestroy
  (JNIEnv *env, jobject thiz ){
	DeInitFmodSystem();
}


void Java_eugen_mymusic_PlayCtrl_updateSoundSystem
  (JNIEnv *env, jobject thiz){
	if( g_pSystem ){
		FMOD_RESULT res = g_pSystem ->update();
		ERRCHECK( res );
	}
}

void Java_eugen_mymusic_PlayCtrl_main
  (JNIEnv *env, jobject thiz){
}

jint Java_eugen_mymusic_PlayCtrl_playSound
  (JNIEnv *env, jobject thiz, jstring filepath){
	if( !g_pSystem )
		return -1;

	Java_eugen_mymusic_PlayCtrl_stopSound( env, thiz, 0 );

	std::string cFilePath = jstring2str( env, filepath );

	LOG_DEBUG("Begin to play sound: %s", cFilePath.c_str() );

	FMOD::Sound* sound = 0;
	FMOD_RESULT result = g_pSystem ->createSound( cFilePath.c_str(), FMOD_SOFTWARE|FMOD_CREATESTREAM , 0, &sound);
    ERRCHECK(result);
    if( sound == 0 )
    	return -2;
    FMOD::Channel* channel = 0;
    g_pSound = sound;
    result = g_pSystem ->playSound(sound, 0, false, &channel);
    ERRCHECK(result);
    if( !channel ){
    	LOG_DEBUG("Fail to create channel" );
    	return -3;
    }

    g_pCurChannel = channel;
    return 0;
}

jboolean Java_eugen_mymusic_PlayCtrl_stopSound
  (JNIEnv *env, jobject thiz, jint soundIdx){
	if( !g_pSystem )
		return false;
	if( g_pCurChannel ){
		g_pCurChannel ->stop();
		g_pCurChannel = 0;
	}
	if( g_pSound ){
		g_pSound ->release();
		g_pSound = 0;
	}
	return true;
}

void Java_eugen_mymusic_PlayCtrl_pauseSound(JNIEnv *env, jobject thiz, jint id, jboolean flag ){
	if( !g_pSystem || !g_pSound || !g_pCurChannel ){
		return;
	}
	g_pCurChannel ->setPaused( flag );
}

jboolean Java_eugen_mymusic_PlayCtrl_isSoundPaused
  (JNIEnv *, jobject, jint){
	if( !g_pSystem || !g_pSound || !g_pCurChannel ){
		return false;
	}
	bool res = false;
	g_pCurChannel ->getPaused( &res );
	return res;
}

jint Java_eugen_mymusic_PlayCtrl_getSoundPos
  (JNIEnv *, jobject, jint){
	if( !g_pSystem || !g_pSound || !g_pCurChannel ){
		return false;
	}
	unsigned int pos = 0;
	g_pCurChannel ->getPosition( &pos, FMOD_TIMEUNIT_MS );
	return (int)pos;
}

jint Java_eugen_mymusic_PlayCtrl_getSoundDur
  (JNIEnv *, jobject, jint){
	if( !g_pSystem || !g_pSound || !g_pCurChannel ){
		return false;
	}
	unsigned pos = 0;
	g_pSound ->getLength( &pos, FMOD_TIMEUNIT_MS );
	return (int)pos;
}

void Java_eugen_mymusic_PlayCtrl_setSoundPos
  (JNIEnv *env, jobject thiz, jint id, jint pos){
	if( !g_pSystem || !g_pSound || !g_pCurChannel ){
		return;
	}
	g_pCurChannel ->setPosition( pos, FMOD_TIMEUNIT_MS );
}

void Java_eugen_mymusic_PlayCtrl_setSoundVol
  (JNIEnv *env, jobject thiz, jint id, jfloat vol){
	if( !g_pSystem || !g_pSound || !g_pCurChannel ){
		return;
	}
	g_pCurChannel ->setVolume( vol );
}

void Java_eugen_mymusic_PlayCtrl_setSoundSpd
  (JNIEnv *env, jobject thiz, jint id, jfloat spd){
	if( !g_pSystem || !g_pSound || !g_pCurChannel ){
		return;
	}
	g_pCurChannel ->setPitch( spd );
}

void Java_eugen_mymusic_PlayCtrl_setSoundLoopPoint
  (JNIEnv *env, jobject thiz, jint id, jint start, jint end){
	if( !g_pSystem || !g_pSound ){
		return;
	}
	if( g_pCurChannel == 0 ){
		LOG_DEBUG( "Fail to set loop points" );
	}else{
		FMOD_RESULT res = g_pCurChannel ->setMode( FMOD_LOOP_NORMAL );
		ERRCHECK( res );
		g_pCurChannel ->setLoopCount( -1 );
		g_pCurChannel ->setLoopPoints( start, FMOD_TIMEUNIT_MS, end, FMOD_TIMEUNIT_MS );
		g_pCurChannel ->setPosition( (unsigned)start, FMOD_TIMEUNIT_MS );
		g_pCurChannel ->setPaused( false );
	}
}

void Java_eugen_mymusic_PlayCtrl_setSoundLoopCount
  (JNIEnv *env, jobject thiz, jint id, jint count){
	if(!g_pSystem || !g_pSound || !g_pCurChannel ){
		return;
	}
	g_pCurChannel ->setLoopCount( count );
}

void Java_eugen_mymusic_PlayCtrl_setSoundLooped
  (JNIEnv *env, jobject thiz, jint id, jboolean flag ){
	if(!g_pSystem || !g_pSound || !g_pCurChannel ){
		return;
	}
	g_pCurChannel ->setMode( flag? FMOD_LOOP_NORMAL: FMOD_LOOP_OFF );
}

jboolean Java_eugen_mymusic_PlayCtrl_getSoundLooped
  (JNIEnv *, jobject, jint){
	if(!g_pSystem || !g_pSound || !g_pCurChannel ){
		return false;
	}
	FMOD_MODE mode;
	g_pCurChannel ->getMode( &mode );
	bool res = (mode&FMOD_LOOP_NORMAL)!=0;
	return res;
//	( flag? FMOD_LOOP_NORMAL: FMOD_LOOP_OFF );
}

void onSoundPlayToEnd( FMOD::Channel* channel ){

}

FMOD_RESULT F_CALLBACK MyMusicCallback(
	FMOD_CHANNELCONTROL *chanControl,
	FMOD_CHANNELCONTROL_TYPE controlType,
	FMOD_CHANNELCONTROL_CALLBACK_TYPE callbackType,
	void *commandData1, void *commandData2){
	if( callbackType == FMOD_CHANNELCONTROL_CALLBACK_END ){
		FMOD::Channel* channel = (FMOD::Channel*)chanControl;
		onSoundPlayToEnd( channel );
	}
	return FMOD_OK;
}
