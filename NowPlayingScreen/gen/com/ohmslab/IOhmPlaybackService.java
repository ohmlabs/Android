/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/jonathanlatterner/git/Android_Player/NowPlayingScreen/src/com/ohmslab/IOhmPlaybackService.aidl
 */
package com.ohmslab;
public interface IOhmPlaybackService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.ohmslab.IOhmPlaybackService
{
private static final java.lang.String DESCRIPTOR = "com.ohmslab.IOhmPlaybackService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.ohmslab.IOhmPlaybackService interface,
 * generating a proxy if needed.
 */
public static com.ohmslab.IOhmPlaybackService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.ohmslab.IOhmPlaybackService))) {
return ((com.ohmslab.IOhmPlaybackService)iin);
}
return new com.ohmslab.IOhmPlaybackService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getNowPlayingPosition:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getNowPlayingPosition();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_isPlaying:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isPlaying();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_playSong:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.playSong(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_playPause:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.playPause();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_prev:
{
data.enforceInterface(DESCRIPTOR);
this.prev();
reply.writeNoException();
return true;
}
case TRANSACTION_next:
{
data.enforceInterface(DESCRIPTOR);
this.next();
reply.writeNoException();
return true;
}
case TRANSACTION_duration:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.duration();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_position:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.position();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setNowPlayingList:
{
data.enforceInterface(DESCRIPTOR);
datamodel.classes.SongList _arg0;
if ((0!=data.readInt())) {
_arg0 = datamodel.classes.SongList.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.setNowPlayingList(_arg0);
reply.writeNoException();
if ((_arg0!=null)) {
reply.writeInt(1);
_arg0.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getNowPlayingList:
{
data.enforceInterface(DESCRIPTOR);
datamodel.classes.SongList _result = this.getNowPlayingList();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_seek:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.seek(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setNowPlayingPosition:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setNowPlayingPosition(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setShuffleMode:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
datamodel.classes.SongList _arg1;
if ((0!=data.readInt())) {
_arg1 = datamodel.classes.SongList.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
this.setShuffleMode(_arg0, _arg1);
reply.writeNoException();
if ((_arg1!=null)) {
reply.writeInt(1);
_arg1.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getShuffleMode:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getShuffleMode();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setRepeatMode:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setRepeatMode(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getRepeatMode:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getRepeatMode();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.ohmslab.IOhmPlaybackService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public int getNowPlayingPosition() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getNowPlayingPosition, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean isPlaying() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isPlaying, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void playSong(int position) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(position);
mRemote.transact(Stub.TRANSACTION_playSong, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public boolean playPause() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_playPause, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void prev() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_prev, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void next() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_next, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public int duration() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_duration, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int position() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_position, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setNowPlayingList(datamodel.classes.SongList songlist) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((songlist!=null)) {
_data.writeInt(1);
songlist.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_setNowPlayingList, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
songlist.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public datamodel.classes.SongList getNowPlayingList() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
datamodel.classes.SongList _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getNowPlayingList, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = datamodel.classes.SongList.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int seek(int pos) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(pos);
mRemote.transact(Stub.TRANSACTION_seek, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setNowPlayingPosition(int position) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(position);
mRemote.transact(Stub.TRANSACTION_setNowPlayingPosition, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void setShuffleMode(int shufflemode, datamodel.classes.SongList songlist) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(shufflemode);
if ((songlist!=null)) {
_data.writeInt(1);
songlist.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_setShuffleMode, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
songlist.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public int getShuffleMode() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getShuffleMode, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setRepeatMode(int repeatmode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(repeatmode);
mRemote.transact(Stub.TRANSACTION_setRepeatMode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public int getRepeatMode() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getRepeatMode, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_getNowPlayingPosition = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_isPlaying = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_playSong = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_playPause = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_prev = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_next = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_duration = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_position = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_setNowPlayingList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_getNowPlayingList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_seek = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_setNowPlayingPosition = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_setShuffleMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_getShuffleMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_setRepeatMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_getRepeatMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
}
public int getNowPlayingPosition() throws android.os.RemoteException;
public boolean isPlaying() throws android.os.RemoteException;
public void playSong(int position) throws android.os.RemoteException;
public boolean playPause() throws android.os.RemoteException;
public void prev() throws android.os.RemoteException;
public void next() throws android.os.RemoteException;
public int duration() throws android.os.RemoteException;
public int position() throws android.os.RemoteException;
public void setNowPlayingList(datamodel.classes.SongList songlist) throws android.os.RemoteException;
public datamodel.classes.SongList getNowPlayingList() throws android.os.RemoteException;
public int seek(int pos) throws android.os.RemoteException;
public void setNowPlayingPosition(int position) throws android.os.RemoteException;
public void setShuffleMode(int shufflemode, datamodel.classes.SongList songlist) throws android.os.RemoteException;
public int getShuffleMode() throws android.os.RemoteException;
public void setRepeatMode(int repeatmode) throws android.os.RemoteException;
public int getRepeatMode() throws android.os.RemoteException;
}
