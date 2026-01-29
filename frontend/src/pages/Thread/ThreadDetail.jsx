import React, {useState, useEffect} from 'react';
import {useLocation, useNavigate, useParams} from 'react-router-dom';
import {ArrowLeft, Send, ArrowBigUp, ArrowBigDown, Loader2} from 'lucide-react';
import {motion, AnimatePresence} from 'framer-motion';
import {toast} from 'react-toastify';

import AppNavbar from '../../components/layout/Navbar/AppNavbar';
import ThreadCard from '../../components/ui/ThreadCard/ThreadCard';
import CountUp from '../../components/ui/CountUp/CountUp';
import {threadService} from '../../services/threadService';

const ThreadDetail = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const {id} = useParams();

    const [thread, setThread] = useState(location.state?.thread || null);
    const [comments, setComments] = useState([]);
    const [newComment, setNewComment] = useState("");
    const [isLoadingThread, setIsLoadingThread] = useState(!thread);
    const [isLoadingComments, setIsLoadingComments] = useState(true);
    const [isPostingComment, setIsPostingComment] = useState(false);

    const calculateTimeAgo = (dateString) => {
        if (!dateString) return "Recentemente";
        const date = new Date(dateString);
        const now = new Date();
        const diffInSeconds = Math.floor((now - date) / 1000);
        if (diffInSeconds < 60) return "Adesso";
        if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)}m fa`;
        if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)}h fa`;
        return `${Math.floor(diffInSeconds / 86400)}g fa`;
    };


    const loadComments = async () => {
        if (!id) return;

        try {
            const res = await threadService.getComments(id);

            let commentsArray = res?.data?.data?.content || res?.data?.data || res?.data || [];

            if (Array.isArray(commentsArray)) {
                const mapped = commentsArray.map(c => {

                    let userVoteValue = 0;
                    if (c.voteType === 'UPVOTE') userVoteValue = 1;
                    if (c.voteType === 'DOWNVOTE') userVoteValue = -1;

                    return {
                        id: c.ID || c.id,
                        author: c.userUsername || c.author || "Anonimo",
                        content: c.content,
                        timeAgo: calculateTimeAgo(c.publicationDate || c.creationDate),
                        profileImage: c.profileImage || c.userProfileImage || null,
                        votes: c.CFU || c.votes || 0,
                        userVote: userVoteValue,
                    };
                });
                setComments(mapped);
            } else {
                setComments([]);
            }
        } catch (error) {
            setComments([]);
        } finally {
            setIsLoadingComments(false);
        }
    };


    useEffect(() => {
        const loadThread = async () => {
            if (thread) {
                setIsLoadingThread(false);
                return;
            }

            try {
                const res = await threadService.getThreadById(id);

                if (res?.data?.data) {
                    const threadData = res.data.data;
                    setThread({
                        id: threadData.ID || threadData.id,
                        content: threadData.content,
                        university: threadData.University || threadData.university || "Generale",
                        topic: threadData.flag || "Generale",
                        author: threadData.userUsername || "Anonimo",
                        timeAgo: calculateTimeAgo(threadData.publicationDate),
                        votes: threadData.CFU || 0,
                    });
                } else {
                    throw new Error("Thread non trovato");
                }
            } catch (error) {
                toast.error("Thread non trovato");
                navigate('/');
            } finally {
                setIsLoadingThread(false);
            }
        };

        loadThread();
    }, [id, thread, navigate]);

    useEffect(() => {
        loadComments();
    }, [id]);

    const handleCommentVote = async (commentId, voteType) => {
        const comment = comments.find(c => c.id === commentId);
        if (!comment) return;

        const previousVote = comment.userVote;
        const previousVotes = comment.votes;

        let newUserVote;
        let voteChange;

        if (voteType === 'UPVOTE') {
            if (previousVote === 1) {
                newUserVote = 0;
                voteChange = -1;
            } else {
                newUserVote = 1;
                voteChange = previousVote === -1 ? 2 : 1;
            }
        } else {
            if (previousVote === -1) {
                newUserVote = 0;
                voteChange = 1;
            } else {
                newUserVote = -1;
                voteChange = previousVote === 1 ? -2 : -1;
            }
        }

        setComments(prev => prev.map(c => c.id === commentId ? {
            ...c,
            userVote: newUserVote,
            votes: c.votes + voteChange
        } : c));

        try {

            await threadService.voteComment(commentId, voteType);
        } catch (error) {
            setComments(prev => prev.map(c => c.id === commentId ? {
                ...c,
                userVote: previousVote,
                votes: previousVotes
            } : c));
            toast.error("Errore nel voto");
        }
    };


    const handleAddComment = async (e) => {
        e.preventDefault();
        if (!newComment.trim() || isPostingComment) return;

        setIsPostingComment(true);

        try {
            const res = await threadService.postComment(thread.id, newComment);

            if (res.status === 200 || res.status === 201) {
                setNewComment(""); // Pulisci input
                await loadComments(); // Ricarica commenti
                toast.success("Commento aggiunto!");
            }
        } catch (error) {
            toast.error("Errore nell'aggiunta del commento");
        } finally {
            setIsPostingComment(false);
        }
    };

    const sortedComments = [...comments].sort((a, b) => b.votes - a.votes);

    if (isLoadingThread) {
        return (<div className="min-h-screen bg-slate-900 flex items-center justify-center text-white flex-col gap-4">
                <Loader2 className="animate-spin" size={40}/>
                <p>Caricamento thread...</p>
            </div>);
    }

    if (!thread) {
        return (<div className="min-h-screen bg-slate-900 flex items-center justify-center text-white flex-col">
                <p>Discussione non trovata.</p>
                <button onClick={() => navigate('/')} className="mt-4 px-4 py-2 bg-younify-blue rounded-xl font-bold">
                    Torna alla Home
                </button>
            </div>);
    }

    return (<div
            className="fixed inset-0 w-full h-full font-sans text-slate-200 selection:bg-younify-blue/30 selection:text-white overflow-hidden flex bg-slate-900/60 backdrop-blur-md lg:bg-transparent lg:backdrop-blur-none">
            <div className="hidden lg:block"><AppNavbar/></div>

            <div
                className="absolute inset-0 overflow-y-auto [&::-webkit-scrollbar]:hidden [-ms-overflow-style:'none'] [scrollbar-width:'none'] z-0">
                <div
                    className="max-w-3xl mx-auto w-full min-h-full flex flex-col pl-0 lg:pl-32 pr-0 lg:pr-8 pt-4 lg:pt-12">
                    <div className="px-4 lg:px-0 mb-6">
                        <motion.div
                            initial={{opacity: 0, x: -20}}
                            animate={{opacity: 1, x: 0}}
                            transition={{delay: 0.3, duration: 0.4}}
                            className="flex items-center gap-4 mb-4"
                        >
                            <button
                                onClick={() => {
                                    navigate('/thread', {state: {shouldRefresh: true}});
                                }}
                                className="p-3 bg-white/5 hover:bg-white/10 rounded-full text-white transition-colors border border-white/5 group"
                            >
                                <ArrowLeft size={24} className="group-hover:-translate-x-1 transition-transform"/>
                            </button>
                            <h1 className="text-2xl font-black text-white tracking-tight">Discussione</h1>
                        </motion.div>

                        <motion.div
                            layoutId={`thread-card-${thread.id}`}
                            className="w-full mb-6"
                            transition={{type: "spring", bounce: 0.2, duration: 0.6}}
                        >
                            <ThreadCard thread={thread} showActions={false}/>
                        </motion.div>
                    </div>

                    <div className="px-4 lg:px-0 pb-40 lg:pb-24">
                        <h3 className="text-lg font-bold text-white mb-4 ml-2 flex items-center gap-2">
                            Commenti
                        </h3>

                        {isLoadingComments ? (<div className="flex items-center justify-center py-20">
                                <Loader2 className="animate-spin text-slate-500" size={32}/>
                            </div>) : (<div className="flex flex-col gap-4">
                                <AnimatePresence mode="popLayout">
                                    {sortedComments.map((comment) => (<motion.div
                                            layout
                                            key={comment.id}
                                            initial={{opacity: 0, y: 10}}
                                            animate={{opacity: 1, y: 0}}
                                            exit={{opacity: 0, scale: 0.9}}
                                            className="bg-slate-900/40 backdrop-blur-md border border-white/5 p-5 rounded-2xl flex gap-4 group hover:border-white/10 transition-colors"
                                        >
                                            <div
                                                className="w-10 h-10 rounded-full border border-white/10 shrink-0 shadow-inner overflow-hidden relative bg-slate-800">
                                                {comment.profileImage ? (<img
                                                        src={comment.profileImage}
                                                        alt={comment.author}
                                                        className="w-full h-full object-cover"
                                                        onError={(e) => {
                                                            e.target.style.display = 'none';
                                                        }}
                                                    />) : null}
                                                <div
                                                    className={`w-full h-full flex items-center justify-center text-sm font-bold text-slate-400 ${comment.profileImage ? 'absolute inset-0 -z-10' : ''}`}>
                                                    {(comment.author || "A").charAt(0).toUpperCase()}
                                                </div>
                                            </div>
                                            <div className="flex-1 flex flex-col gap-2">
                                                <div className="flex justify-between items-baseline">
                                                    <span
                                                        className="font-bold text-white text-sm">{comment.author}</span>
                                                    <span className="text-xs text-slate-500">{comment.timeAgo}</span>
                                                </div>
                                                <p className="text-slate-300 text-sm leading-relaxed mb-1">{comment.content}</p>
                                                <div className="flex items-center justify-end gap-3 mt-1">
                                                    <div
                                                        className="flex items-center gap-1 bg-black/20 px-2 py-1 rounded-lg border border-white/5">
                                                        <button
                                                            onClick={() => handleCommentVote(comment.id, 'DOWNVOTE')}
                                                            className={`p-1 rounded hover:bg-white/10 transition-colors ${comment.userVote === -1 ? 'text-rose-500' : 'text-slate-500 hover:text-rose-400'}`}
                                                        >
                                                            <ArrowBigDown size={18}
                                                                          fill={comment.userVote === -1 ? "currentColor" : "none"}/>
                                                        </button>
                                                        <div
                                                            className={`flex items-baseline gap-1 font-mono font-bold text-xs min-w-[30px] justify-center ${comment.userVote === 1 ? 'text-emerald-400' : comment.userVote === -1 ? 'text-rose-500' : 'text-slate-400'}`}>
                                                            <CountUp from={0} to={comment.votes} duration={0.5}/>
                                                            <span className="text-[9px] font-sans">CFU</span>
                                                        </div>
                                                        <button
                                                            onClick={() => handleCommentVote(comment.id, 'UPVOTE')}
                                                            className={`p-1 rounded hover:bg-white/10 transition-colors ${comment.userVote === 1 ? 'text-emerald-400' : 'text-slate-500 hover:text-emerald-400'}`}
                                                        >
                                                            <ArrowBigUp size={18}
                                                                        fill={comment.userVote === 1 ? "currentColor" : "none"}/>
                                                        </button>
                                                    </div>
                                                </div>
                                            </div>
                                        </motion.div>))}
                                </AnimatePresence>

                                {comments.length === 0 && !isLoadingComments && (
                                    <div className="text-center py-10 text-slate-500">
                                        <p>Nessun commento ancora. Sii il primo!</p>
                                    </div>)}
                            </div>)}
                    </div>
                </div>
            </div>

            <motion.div
                initial={{opacity: 0, y: 20}}
                animate={{opacity: 1, y: 0}}
                transition={{delay: 0.5}}
                className="fixed bottom-0 left-0 right-0 z-30 pointer-events-none pb-4 pt-4 lg:pb-6 lg:pl-32 bg-transparent"
            >
                <div className="max-w-3xl mx-auto w-full px-4 lg:px-1 pointer-events-auto">
                    <form onSubmit={handleAddComment}
                          className="relative flex items-center shadow-2xl shadow-black/50 rounded-full group">
                        <input
                            type="text"
                            maxLength={500}
                            value={newComment}
                            onChange={(e) => setNewComment(e.target.value)}
                            placeholder="Scrivi un commento..."
                            disabled={isPostingComment}
                            className="w-full bg-slate-900/60 backdrop-blur-xl border border-white/10 text-white rounded-full py-4 pl-6 pr-14 focus:outline-none focus:border-younify-blue focus:ring-1 focus:ring-younify-blue transition-all placeholder:text-slate-500 shadow-inner disabled:opacity-50"
                        />
                        <button
                            type="submit"
                            disabled={!newComment.trim() || isPostingComment}
                            className="absolute right-2 p-2.5 bg-younify-blue text-white rounded-full hover:bg-blue-600 disabled:opacity-50 disabled:bg-slate-800 transition-all active:scale-95"
                        >
                            {isPostingComment ? <Loader2 className="animate-spin" size={18}/> : <Send size={18}/>}
                        </button>
                    </form>
                </div>
            </motion.div>
        </div>);
};

export default ThreadDetail;
